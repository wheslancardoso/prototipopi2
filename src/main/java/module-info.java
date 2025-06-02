module com.teatro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;

    opens com.teatro to javafx.fxml;
    opens com.teatro.controller to javafx.fxml;
    opens com.teatro.view to javafx.fxml;
    
    exports com.teatro;
    exports com.teatro.controller;
    exports com.teatro.view;
    exports com.teatro.model;
    exports com.teatro.dao;
    exports com.teatro.database;
}