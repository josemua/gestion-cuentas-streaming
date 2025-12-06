package com.josemuadev.trabajofinal.controller;

import com.josemuadev.trabajofinal.model.CuentaStreaming;
import com.josemuadev.trabajofinal.model.PlataformaStreaming;
import com.josemuadev.trabajofinal.negocio.AdminCuentas;
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
import java.util.List;
import java.util.ResourceBundle;

public class CuentasController implements Initializable {

    @FXML private TableView<CuentaStreaming> tablaCuentas;
    @FXML private TableColumn<CuentaStreaming, String> colId;
    @FXML private TableColumn<CuentaStreaming, String> colEmail;
    @FXML private TableColumn<CuentaStreaming, String> colPlataforma;
    @FXML private TableColumn<CuentaStreaming, String> colPlan;
    @FXML private TableColumn<CuentaStreaming, String> colEspacios;
    @FXML private TableColumn<CuentaStreaming, String> colNotas;

    @FXML private ComboBox<PlataformaStreaming> cmbFiltroPlataforma;
    @FXML private ComboBox<PlataformaStreaming> cmbPlataforma;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPassword;
    @FXML private TextField txtPlan;
    @FXML private Spinner<Integer> spnMaxEspacios;
    @FXML private TextArea txtNotas;
    @FXML private TitledPane panelEdicion;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    private AdminCuentas adminCuentas;
    private AdminPlataformas adminPlataformas;
    private ObservableList<CuentaStreaming> listaCuentas;
    private CuentaStreaming cuentaSeleccionada;
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GestorPrincipal gestor = GestorPrincipal.getInstance();
        adminCuentas = gestor.getAdminCuentas();
        adminPlataformas = gestor.getAdminPlataformas();
        listaCuentas = FXCollections.observableArrayList();

        configurarTabla();
        configurarSpinner();
        cargarPlataformas();
        cargarCuentas();

        tablaCuentas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> seleccionarCuenta(newVal)
        );
    }

    private void configurarTabla() {
        colPlataforma.setCellValueFactory(cellData -> {
            PlataformaStreaming plat = cellData.getValue().getPlataforma();
            return new SimpleStringProperty(plat != null ? plat.getNombre() : "Sin asignar");
        });

        colEspacios.setCellValueFactory(cellData -> {
            CuentaStreaming cuenta = cellData.getValue();
            return new SimpleStringProperty(cuenta.getEspaciosOcupados() + "/" + cuenta.getMaxEspacios());
        });
    }

    private void configurarSpinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 4);
        spnMaxEspacios.setValueFactory(valueFactory);
    }

    private void cargarPlataformas() {
        List<PlataformaStreaming> plataformas = adminPlataformas.listarPlataformas();
        cmbPlataforma.setItems(FXCollections.observableArrayList(plataformas));

        ObservableList<PlataformaStreaming> filtroList = FXCollections.observableArrayList();
        filtroList.add(null); // Representa "Todas"
        filtroList.addAll(plataformas);
        cmbFiltroPlataforma.setItems(filtroList);

        cmbFiltroPlataforma.setButtonCell(new ListCell<PlataformaStreaming>() {
            @Override
            protected void updateItem(PlataformaStreaming item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? "Todas las plataformas" : item.getNombre());
            }
        });
        cmbFiltroPlataforma.setCellFactory(lv -> new ListCell<PlataformaStreaming>() {
            @Override
            protected void updateItem(PlataformaStreaming item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? "Todas las plataformas" : item.getNombre());
            }
        });
    }

    private void cargarCuentas() {
        listaCuentas.setAll(adminCuentas.listarCuentas());
        tablaCuentas.setItems(listaCuentas);
        tablaCuentas.refresh();
    }

    public void actualizarVista() {
        cargarCuentas();
    }

    private void seleccionarCuenta(CuentaStreaming cuenta) {
        if (cuenta != null) {
            cuentaSeleccionada = cuenta;
            cmbPlataforma.setValue(cuenta.getPlataforma());
            txtEmail.setText(cuenta.getEmail());
            txtPassword.setText(cuenta.getPassword());
            txtPlan.setText(cuenta.getPlan());
            spnMaxEspacios.getValueFactory().setValue(cuenta.getMaxEspacios());
            txtNotas.setText(cuenta.getNotas());
            panelEdicion.setExpanded(true);
            btnEliminar.setVisible(true);
            modoEdicion = true;

            cmbPlataforma.setDisable(true);
        }
    }

    @FXML
    private void nuevaCuenta() {
        limpiarFormulario();
        panelEdicion.setExpanded(true);
        cmbPlataforma.setDisable(false);
        cmbPlataforma.requestFocus();
        modoEdicion = false;
        cuentaSeleccionada = null;
        btnEliminar.setVisible(false);
    }

    @FXML
    private void guardarCuenta() {
        try {
            PlataformaStreaming plataforma = cmbPlataforma.getValue();
            String email = txtEmail.getText();
            String password = txtPassword.getText();
            String plan = txtPlan.getText();
            int maxEspacios = spnMaxEspacios.getValue();
            String notas = txtNotas.getText();

            if (modoEdicion && cuentaSeleccionada != null) {
                adminCuentas.editarCuenta(cuentaSeleccionada.getId(), email, password, plan, maxEspacios, notas);
                mostrarMensaje("Cuenta actualizada correctamente", false);
            } else {
                adminCuentas.agregarCuenta(email, password, plan, maxEspacios, notas, plataforma);
                mostrarMensaje("Cuenta registrada correctamente", false);
            }

            GestorPrincipal.getInstance().guardarDatos();
            cargarCuentas();
            cancelarEdicion();

        } catch (StreamingException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (Exception e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), true);
        }
    }

    @FXML
    private void eliminarCuenta() {
        if (cuentaSeleccionada == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar cuenta?");
        confirmacion.setContentText("Se eliminará la cuenta: " + cuentaSeleccionada.getEmail());

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    adminCuentas.eliminarCuenta(cuentaSeleccionada.getId());
                    GestorPrincipal.getInstance().guardarDatos();
                    cargarCuentas();
                    cancelarEdicion();
                    mostrarMensaje("Cuenta eliminada correctamente", false);
                } catch (StreamingException e) {
                    mostrarMensaje(e.getMessage(), true);
                } catch (Exception e) {
                    mostrarMensaje("Error al guardar: " + e.getMessage(), true);
                }
            }
        });
    }

    @FXML
    private void filtrarPorPlataforma() {
        PlataformaStreaming plataforma = cmbFiltroPlataforma.getValue();
        if (plataforma == null) {
            cargarCuentas();
        } else {
            listaCuentas.setAll(adminCuentas.listarCuentasPorPlataforma(plataforma));
        }
    }

    @FXML
    private void mostrarTodas() {
        cmbFiltroPlataforma.setValue(null);
        cargarCuentas();
    }

    @FXML
    private void cancelarEdicion() {
        limpiarFormulario();
        panelEdicion.setExpanded(false);
        tablaCuentas.getSelectionModel().clearSelection();
        cuentaSeleccionada = null;
        modoEdicion = false;
        btnEliminar.setVisible(false);
        cmbPlataforma.setDisable(false);
    }

    private void limpiarFormulario() {
        cmbPlataforma.setValue(null);
        txtEmail.clear();
        txtPassword.clear();
        txtPlan.clear();
        spnMaxEspacios.getValueFactory().setValue(4);
        txtNotas.clear();
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }
}
