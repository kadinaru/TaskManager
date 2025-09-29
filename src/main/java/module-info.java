module com.example.taskmanager {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.sql;

        opens com.example.taskmanager.dao.impl to javafx.fxml;
        opens com.example.taskmanager.tasklist to javafx.fxml;
        opens com.example.taskmanager.model to javafx.fxml;
        exports com.example.taskmanager.tasklist;
}
