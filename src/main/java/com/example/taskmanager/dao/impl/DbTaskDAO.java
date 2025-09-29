package com.example.taskmanager.dao.impl;

import com.example.taskmanager.dao.TaskDAO;
import com.example.taskmanager.model.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DbTaskDAO implements TaskDAO {
    private final String url;

    public DbTaskDAO() {
        try {
            InputStream is = getClass().getResourceAsStream("/tasks.db");
            if (is == null) throw new RuntimeException("tasks.db not found in resources");

            File temp = new File("tasks_copy.db");
            try (FileOutputStream fos = new FileOutputStream(temp)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            }

            url = "jdbc:sqlite:" + temp.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        init();
    }

    private void init() {
        String ddl = """
            CREATE TABLE IF NOT EXISTS tasks (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name TEXT,
              hours INTEGER,
              minutes INTEGER,
              status TEXT,
              executor TEXT,
              dueDate TEXT,
              context TEXT
            );
            """;
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement()) {
            s.execute(ddl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection c = DriverManager.getConnection(url);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Task t = new Task();
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                t.setHours(rs.getInt("hours"));
                t.setMinutes(rs.getInt("minutes"));
                t.setStatus(rs.getString("status"));
                t.setExecutor(rs.getString("executor"));
                String due = rs.getString("dueDate");
                t.setDueDate(due == null ? null : LocalDate.parse(due));
                t.setContext(rs.getString("context"));
                list.add(t);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public Task getTaskById(int id) {return null;}

    @Override
    public void addTask(Task task) {}

    @Override
    public void updateTask(Task task) {}

    @Override
    public void deleteTask(int id) {}
}
