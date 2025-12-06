package com.josemuadev.trabajofinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/josemuadev/trabajofinal/principal.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 700);

        primaryStage.setTitle("Sistema de Gestión de Cuentas de Streaming");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);

        primaryStage.show();

        System.out.println("==============================================");
        System.out.println("  Sistema de Gestión de Cuentas de Streaming  ");
        System.out.println("  Técnicas de Programación - UdeA 2025        ");
        System.out.println("==============================================");
    }

    @Override
    public void stop() throws Exception {
        try {
            com.josemuadev.trabajofinal.negocio.GestorPrincipal.getInstance().guardarDatos();
            System.out.println("Datos guardados automáticamente al cerrar.");
        } catch (Exception e) {
            System.err.println("Error al guardar datos: " + e.getMessage());
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
