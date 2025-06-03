module com.teatro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    // Abre os pacotes necessários para reflexão
    opens com.teatro to javafx.fxml, org.slf4j, ch.qos.logback.classic;
    opens com.teatro.controller to javafx.fxml;
    opens com.teatro.view to javafx.fxml;
    
    // Exporta os pacotes necessários
    exports com.teatro;
    exports com.teatro.controller;
    exports com.teatro.view;
    exports com.teatro.model;
    exports com.teatro.dao;
    exports com.teatro.database;
}