package com.josemuadev.trabajofinal.controller;

import com.josemuadev.trabajofinal.model.Cliente;
import com.josemuadev.trabajofinal.model.CuentaStreaming;
import com.josemuadev.trabajofinal.model.EspacioCompartido;
import com.josemuadev.trabajofinal.negocio.*;
import com.josemuadev.trabajofinal.util.StreamingException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class EspaciosController implements Initializable {

    @FXML private TableView<EspacioCompartido> tablaEspacios;
    @FXML private TableColumn<EspacioCompartido, String> colId;
    @FXML private TableColumn<EspacioCompartido, String> colCliente;
    @FXML private TableColumn<EspacioCompartido, String> colCuenta;
    @FXML private TableColumn<EspacioCompartido, String> colPlataforma;
    @FXML private TableColumn<EspacioCompartido, String> colPrecio;
    @FXML private TableColumn<EspacioCompartido, String> colFecha;
    @FXML private TableColumn<EspacioCompartido, String> colDeuda;

    @FXML private ComboBox<Cliente> cmbFiltroCliente;
    @FXML private ComboBox<CuentaStreaming> cmbFiltroCuenta;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private ComboBox<CuentaStreaming> cmbCuenta;
    @FXML private TextField txtPrecio;
    @FXML private TextArea txtNotas;
    @FXML private TitledPane panelEdicion;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    private AdminEspacios adminEspacios;
    private AdminClientes adminClientes;
    private AdminCuentas adminCuentas;
    private ObservableList<EspacioCompartido> listaEspacios;
    private EspacioCompartido espacioSeleccionado;
    private boolean modoEdicion = false;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GestorPrincipal gestor = GestorPrincipal.getInstance();
        adminEspacios = gestor.getAdminEspacios();
        adminClientes = gestor.getAdminClientes();
        adminCuentas = gestor.getAdminCuentas();
        listaEspacios = FXCollections.observableArrayList();

        configurarTabla();
        cargarCombos();
        cargarEspacios();

        tablaEspacios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> seleccionarEspacio(newVal)
        );
    }

    private void configurarTabla() {
        colCliente.setCellValueFactory(cellData -> {
            Cliente cliente = cellData.getValue().getCliente();
            return new SimpleStringProperty(cliente != null ? cliente.getNombre() : "Sin asignar");
        });

        colCuenta.setCellValueFactory(cellData -> {
            CuentaStreaming cuenta = cellData.getValue().getCuenta();
            return new SimpleStringProperty(cuenta != null ? cuenta.getEmail() : "Sin asignar");
        });

        colPlataforma.setCellValueFactory(cellData -> {
            CuentaStreaming cuenta = cellData.getValue().getCuenta();
            if (cuenta != null && cuenta.getPlataforma() != null) {
                return new SimpleStringProperty(cuenta.getPlataforma().getNombre());
            }
            return new SimpleStringProperty("-");
        });

        colPrecio.setCellValueFactory(cellData -> {
            return new SimpleStringProperty("$" + String.format("%.0f", cellData.getValue().getPrecioMensual()));
        });

        colFecha.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getFechaInicio().format(FORMATO_FECHA));
        });

        colDeuda.setCellValueFactory(cellData -> {
            double deuda = cellData.getValue().getDeudaTotal();
            return new SimpleStringProperty(deuda > 0 ? "$" + String.format("%.0f", deuda) : "-");
        });
    }

    private void cargarCombos() {
        List<Cliente> clientes = adminClientes.listarClientes();
        List<CuentaStreaming> cuentas = adminCuentas.listarCuentasConEspaciosDisponibles();
        List<CuentaStreaming> todasCuentas = adminCuentas.listarCuentas();

        cmbCliente.setItems(FXCollections.observableArrayList(clientes));
        cmbCuenta.setItems(FXCollections.observableArrayList(cuentas));

        ObservableList<Cliente> filtroClientes = FXCollections.observableArrayList();
        filtroClientes.add(null);
        filtroClientes.addAll(clientes);
        cmbFiltroCliente.setItems(filtroClientes);

        ObservableList<CuentaStreaming> filtroCuentas = FXCollections.observableArrayList();
        filtroCuentas.add(null);
        filtroCuentas.addAll(todasCuentas);
        cmbFiltroCuenta.setItems(filtroCuentas);

        configurarCeldaCombo(cmbFiltroCliente, "Todos los clientes");
        configurarCeldaCombo(cmbFiltroCuenta, "Todas las cuentas");
    }

    private <T> void configurarCeldaCombo(ComboBox<T> combo, String textoNulo) {
        combo.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? textoNulo : item.toString());
            }
        });
        combo.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? textoNulo : item.toString());
            }
        });
    }

    private void cargarEspacios() {
        listaEspacios.setAll(adminEspacios.listarEspacios());
        tablaEspacios.setItems(listaEspacios);
    }

    private void seleccionarEspacio(EspacioCompartido espacio) {
        if (espacio != null) {
            espacioSeleccionado = espacio;
            cmbCliente.setValue(espacio.getCliente());
            cmbCuenta.setValue(espacio.getCuenta());
            txtPrecio.setText(String.valueOf(espacio.getPrecioMensual()));
            txtNotas.setText(espacio.getNotas());
            panelEdicion.setExpanded(true);
            btnEliminar.setVisible(true);
            modoEdicion = true;

            cmbCliente.setDisable(true);
            cmbCuenta.setDisable(true);
        }
    }

    @FXML
    private void nuevoEspacio() {
        limpiarFormulario();
        cmbCuenta.setItems(FXCollections.observableArrayList(
                adminCuentas.listarCuentasConEspaciosDisponibles()));
        panelEdicion.setExpanded(true);
        cmbCliente.setDisable(false);
        cmbCuenta.setDisable(false);
        cmbCliente.requestFocus();
        modoEdicion = false;
        espacioSeleccionado = null;
        btnEliminar.setVisible(false);
    }

    @FXML
    private void guardarEspacio() {
        try {
            double precio;
            try {
                precio = Double.parseDouble(txtPrecio.getText().trim());
            } catch (NumberFormatException e) {
                throw new StreamingException("El precio debe ser un número válido.");
            }

            String notas = txtNotas.getText();

            if (modoEdicion && espacioSeleccionado != null) {
                adminEspacios.editarEspacio(espacioSeleccionado.getId(), precio, notas);
                mostrarMensaje("Espacio actualizado correctamente", false);
            } else {
                Cliente cliente = cmbCliente.getValue();
                CuentaStreaming cuenta = cmbCuenta.getValue();
                adminEspacios.crearEspacio(cliente, cuenta, precio, notas);
                mostrarMensaje("Espacio creado correctamente", false);
            }

            GestorPrincipal.getInstance().guardarDatos();
            cargarEspacios();
            cargarCombos();
            cancelarEdicion();

        } catch (StreamingException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (Exception e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), true);
        }
    }

    @FXML
    private void eliminarEspacio() {
        if (espacioSeleccionado == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar espacio?");
        confirmacion.setContentText("Se eliminará el espacio del cliente: " +
                (espacioSeleccionado.getCliente() != null ? espacioSeleccionado.getCliente().getNombre() : "Sin cliente"));

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    adminEspacios.eliminarEspacio(espacioSeleccionado.getId());
                    GestorPrincipal.getInstance().guardarDatos();
                    cargarEspacios();
                    cargarCombos();
                    cancelarEdicion();
                    mostrarMensaje("Espacio eliminado correctamente", false);
                } catch (StreamingException e) {
                    mostrarMensaje(e.getMessage(), true);
                } catch (Exception e) {
                    mostrarMensaje("Error al guardar: " + e.getMessage(), true);
                }
            }
        });
    }

    @FXML
    private void filtrar() {
        Cliente cliente = cmbFiltroCliente.getValue();
        CuentaStreaming cuenta = cmbFiltroCuenta.getValue();

        List<EspacioCompartido> resultado;

        if (cliente != null) {
            resultado = adminEspacios.listarEspaciosPorCliente(cliente);
        } else if (cuenta != null) {
            resultado = adminEspacios.listarEspaciosPorCuenta(cuenta);
        } else {
            resultado = adminEspacios.listarEspacios();
        }

        listaEspacios.setAll(resultado);
    }

    @FXML
    private void limpiarFiltros() {
        cmbFiltroCliente.setValue(null);
        cmbFiltroCuenta.setValue(null);
        cargarEspacios();
    }

    @FXML
    private void cancelarEdicion() {
        limpiarFormulario();
        panelEdicion.setExpanded(false);
        tablaEspacios.getSelectionModel().clearSelection();
        espacioSeleccionado = null;
        modoEdicion = false;
        btnEliminar.setVisible(false);
        cmbCliente.setDisable(false);
        cmbCuenta.setDisable(false);
    }

    private void limpiarFormulario() {
        cmbCliente.setValue(null);
        cmbCuenta.setValue(null);
        txtPrecio.clear();
        txtNotas.clear();
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }
}
