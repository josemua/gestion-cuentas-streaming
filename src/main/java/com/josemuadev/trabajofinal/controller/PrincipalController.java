package com.josemuadev.trabajofinal.controller;

import com.josemuadev.trabajofinal.negocio.GestorPrincipal;
import com.josemuadev.trabajofinal.util.StreamingException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    @FXML private StackPane contenedorPrincipal;
    @FXML private Label lblEstado;
    @FXML private Label lblTotalClientes;
    @FXML private Label lblTotalCuentas;
    @FXML private Label lblTotalEspacios;
    @FXML private Label lblDeudaTotal;

    private GestorPrincipal gestor;
    private Node vistaInicial;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gestor = GestorPrincipal.getInstance();

        if (!contenedorPrincipal.getChildren().isEmpty()) {
            vistaInicial = contenedorPrincipal.getChildren().get(0);
        }

        try {
            gestor.cargarDatos();
            lblEstado.setText("Datos cargados correctamente");
        } catch (IOException e) {
            lblEstado.setText("No se encontraron datos previos - Sistema nuevo");
        }

        actualizarEstadisticas();
    }

    public void actualizarEstadisticas() {
        lblTotalClientes.setText(String.valueOf(gestor.getAdminClientes().getTotalClientes()));
        lblTotalCuentas.setText(String.valueOf(gestor.getAdminCuentas().getTotalCuentas()));

        int espaciosOcupados = gestor.getAdminEspacios().getTotalEspacios();
        int espaciosTotales = gestor.getAdminCuentas().getTotalEspaciosMaximos();
        lblTotalEspacios.setText(espaciosOcupados + "/" + espaciosTotales);

        lblDeudaTotal.setText("$" + String.format("%.0f", gestor.getAdminEspacios().getDeudaTotal()));
    }

    @FXML
    private void mostrarClientes() {
        cargarVista("/com/josemuadev/trabajofinal/clientes.fxml", "Clientes");
    }

    @FXML
    private void mostrarPlataformas() {
        cargarVista("/com/josemuadev/trabajofinal/plataformas.fxml", "Plataformas");
    }

    @FXML
    private void mostrarCuentas() {
        cargarVista("/com/josemuadev/trabajofinal/cuentas.fxml", "Cuentas");
    }

    @FXML
    private void mostrarEspacios() {
        cargarVista("/com/josemuadev/trabajofinal/espacios.fxml", "Espacios");
    }

    @FXML
    private void mostrarPagos() {
        cargarVista("/com/josemuadev/trabajofinal/pagos.fxml", "Pagos");
    }

    @FXML
    private void guardarDatos() {
        try {
            gestor.guardarDatos();
            lblEstado.setText("Datos guardados correctamente");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Datos guardados correctamente.");
        } catch (IOException e) {
            lblEstado.setText("Error al guardar datos");
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron guardar los datos: " + e.getMessage());
        }
    }

    @FXML
    private void cargarEjemplo() {
        try {
            gestor.cargarDatosEjemplo();
            actualizarEstadisticas();
            lblEstado.setText("Datos de ejemplo cargados");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Datos de ejemplo cargados correctamente.");
        } catch (StreamingException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar datos de ejemplo: " + e.getMessage());
        }
    }

    @FXML
    private void clickCajitaClientes() {
        mostrarClientes();
    }

    @FXML
    private void clickCajitaCuentas() {
        mostrarCuentas();
    }

    @FXML
    private void clickCajitaEspacios() {
        mostrarEspacios();
    }

    @FXML
    private void clickCajitaPagos() {
        mostrarPagos();
    }

    private void cargarVista(String rutaFxml, String nombreVista) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Node vista = loader.load();
            contenedorPrincipal.getChildren().setAll(vista);
            lblEstado.setText("Vista: " + nombreVista);
        } catch (IOException e) {
            System.err.println("Error al cargar vista '" + nombreVista + "': " + e.getMessage());
            lblEstado.setText("Error al cargar vista: " + nombreVista);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista: " + e.getMessage());
        }
    }

    public void volverAlDashboard() {
        if (vistaInicial != null) {
            contenedorPrincipal.getChildren().setAll(vistaInicial);
            actualizarEstadisticas();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
