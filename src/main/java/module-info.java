module com.teatro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires transitive javafx.graphics;
    requires javafx.base;

    exports com.teatro;
    exports com.teatro.model;
    exports com.teatro.dao;
    exports com.teatro.service;
    exports com.teatro.controller;
    exports com.teatro.observer;
    exports com.teatro.database;
    exports com.teatro.util;
    exports com.teatro.model.builder;
    exports com.teatro.view.util;
    
    opens com.teatro to javafx.fxml;
    opens com.teatro.controller to javafx.fxml;
    opens com.teatro.model to javafx.base;
    opens com.teatro.view to javafx.fxml;
    opens com.teatro.view.util to javafx.fxml;
}