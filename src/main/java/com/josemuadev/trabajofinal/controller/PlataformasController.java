package com.josemuadev.trabajofinal.controller;

import com.josemuadev.trabajofinal.model.PlataformaStreaming;
import com.josemuadev.trabajofinal.negocio.AdminPlataformas;
import com.josemuadev.trabajofinal.negocio.GestorPrincipal;
import com.josemuadev.trabajofinal.util.StreamingException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class PlataformasController implements Initializable {

    @FXML private TableView<PlataformaStreaming> tablaPlataformas;
    @FXML private TableColumn<PlataformaStreaming, String> colId;
    @FXML private TableColumn<PlataformaStreaming, String> colNombre;
    @FXML private TableColumn<PlataformaStreaming, String> colNotas;
    @FXML private TableColumn<PlataformaStreaming, String> colCuentas;

    @FXML private TextField txtNombre;
    @FXML private TextArea txtNotas;
    @FXML private TitledPane panelEdicion;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    private AdminPlataformas adminPlataformas;
    private ObservableList<PlataformaStreaming> listaPlataformas;
    private PlataformaStreaming plataformaSeleccionada;
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminPlataformas = GestorPrincipal.getInstance().getAdminPlataformas();
        listaPlataformas = FXCollections.observableArrayList();

        configurarTabla();
        cargarPlataformas();

        tablaPlataformas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> seleccionarPlataforma(newVal)
        );
    }

    private void configurarTabla() {
        colCuentas.setCellValueFactory(cellData -> {
            int cuentas = cellData.getValue().getCuentas().size();
            return new SimpleStringProperty(String.valueOf(cuentas));
        });
    }

    private void cargarPlataformas() {
        listaPlataformas.setAll(adminPlataformas.listarPlataformas());
        tablaPlataformas.setItems(listaPlataformas);
    }

    private void seleccionarPlataforma(PlataformaStreaming plataforma) {
        if (plataforma != null) {
            plataformaSeleccionada = plataforma;
            txtNombre.setText(plataforma.getNombre());
            txtNotas.setText(plataforma.getNotas());
            panelEdicion.setExpanded(true);
            btnEliminar.setVisible(true);
            modoEdicion = true;
        }
    }

    @FXML
    private void nuevaPlataforma() {
        limpiarFormulario();
        panelEdicion.setExpanded(true);
        txtNombre.requestFocus();
        modoEdicion = false;
        plataformaSeleccionada = null;
        btnEliminar.setVisible(false);
    }

    @FXML
    private void guardarPlataforma() {
        try {
            String nombre = txtNombre.getText();
            String notas = txtNotas.getText();

            if (modoEdicion && plataformaSeleccionada != null) {
                adminPlataformas.editarPlataforma(plataformaSeleccionada.getId(), nombre, notas);
                mostrarMensaje("Plataforma actualizada correctamente", false);
            } else {
                adminPlataformas.agregarPlataforma(nombre, notas);
                mostrarMensaje("Plataforma registrada correctamente", false);
            }

            GestorPrincipal.getInstance().guardarDatos();
            cargarPlataformas();
            cancelarEdicion();

        } catch (StreamingException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (Exception e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), true);
        }
    }

    @FXML
    private void eliminarPlataforma() {
        if (plataformaSeleccionada == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar plataforma?");
        confirmacion.setContentText("Se eliminará la plataforma: " + plataformaSeleccionada.getNombre());

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    adminPlataformas.eliminarPlataforma(plataformaSeleccionada.getId());
                    GestorPrincipal.getInstance().guardarDatos();
                    cargarPlataformas();
                    cancelarEdicion();
                    mostrarMensaje("Plataforma eliminada correctamente", false);
                } catch (StreamingException e) {
                    mostrarMensaje(e.getMessage(), true);
                } catch (Exception e) {
                    mostrarMensaje("Error al guardar: " + e.getMessage(), true);
                }
            }
        });
    }

    @FXML
    private void cancelarEdicion() {
        limpiarFormulario();
        panelEdicion.setExpanded(false);
        tablaPlataformas.getSelectionModel().clearSelection();
        plataformaSeleccionada = null;
        modoEdicion = false;
        btnEliminar.setVisible(false);
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtNotas.clear();
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }
}
