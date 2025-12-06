module com.josemuadev.trabajofinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.josemuadev.trabajofinal to javafx.fxml;
    opens com.josemuadev.trabajofinal.controller to javafx.fxml;
    opens com.josemuadev.trabajofinal.model to javafx.base;

    exports com.josemuadev.trabajofinal;
    exports com.josemuadev.trabajofinal.model;
    exports com.josemuadev.trabajofinal.negocio;
    exports com.josemuadev.trabajofinal.dao;
    exports com.josemuadev.trabajofinal.controller;
    exports com.josemuadev.trabajofinal.util;
}