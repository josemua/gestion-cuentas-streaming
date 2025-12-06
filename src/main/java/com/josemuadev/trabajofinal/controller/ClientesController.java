package com.josemuadev.trabajofinal.controller;

import com.josemuadev.trabajofinal.model.Cliente;
import com.josemuadev.trabajofinal.negocio.AdminClientes;
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

public class ClientesController implements Initializable {

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colNotas;
    @FXML private TableColumn<Cliente, String> colEspacios;

    @FXML private TextField txtBuscar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtNotas;
    @FXML private TitledPane panelEdicion;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    private AdminClientes adminClientes;
    private ObservableList<Cliente> listaClientes;
    private Cliente clienteSeleccionado;
    private boolean modoEdicion = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminClientes = GestorPrincipal.getInstance().getAdminClientes();
        listaClientes = FXCollections.observableArrayList();

        configurarTabla();
        cargarClientes();

        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> seleccionarCliente(newVal)
        );
    }

    private void configurarTabla() {
        colEspacios.setCellValueFactory(cellData -> {
            int espacios = cellData.getValue().getEspacios().size();
            return new SimpleStringProperty(String.valueOf(espacios));
        });
    }

    private void cargarClientes() {
        listaClientes.setAll(adminClientes.listarClientes());
        tablaClientes.setItems(listaClientes);
    }

    private void seleccionarCliente(Cliente cliente) {
        if (cliente != null) {
            clienteSeleccionado = cliente;
            txtNombre.setText(cliente.getNombre());
            txtTelefono.setText(cliente.getTelefono());
            txtNotas.setText(cliente.getNotas());
            panelEdicion.setExpanded(true);
            btnEliminar.setVisible(true);
            modoEdicion = true;
        }
    }

    @FXML
    private void nuevoCliente() {
        limpiarFormulario();
        panelEdicion.setExpanded(true);
        txtNombre.requestFocus();
        modoEdicion = false;
        clienteSeleccionado = null;
        btnEliminar.setVisible(false);
    }

    @FXML
    private void guardarCliente() {
        try {
            String nombre = txtNombre.getText();
            String telefono = txtTelefono.getText();
            String notas = txtNotas.getText();

            if (modoEdicion && clienteSeleccionado != null) {
                adminClientes.editarCliente(clienteSeleccionado.getId(), nombre, telefono, notas);
                mostrarMensaje("Cliente actualizado correctamente", false);
            } else {
                adminClientes.agregarCliente(nombre, telefono, notas);
                mostrarMensaje("Cliente registrado correctamente", false);
            }

            GestorPrincipal.getInstance().guardarDatos();
            cargarClientes();
            cancelarEdicion();

        } catch (StreamingException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (Exception e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), true);
        }
    }

    @FXML
    private void eliminarCliente() {
        if (clienteSeleccionado == null) return;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar cliente?");
        confirmacion.setContentText("Se eliminará el cliente: " + clienteSeleccionado.getNombre());

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    adminClientes.eliminarCliente(clienteSeleccionado.getId());
                    GestorPrincipal.getInstance().guardarDatos();
                    cargarClientes();
                    cancelarEdicion();
                    mostrarMensaje("Cliente eliminado correctamente", false);
                } catch (StreamingException e) {
                    mostrarMensaje(e.getMessage(), true);
                } catch (Exception e) {
                    mostrarMensaje("Error al guardar: " + e.getMessage(), true);
                }
            }
        });
    }

    @FXML
    private void buscar() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            cargarClientes();
        } else {
            List<Cliente> resultados = adminClientes.buscarClientesPorNombre(termino);
            Cliente porTelefono = adminClientes.buscarClientePorTelefono(termino);
            if (porTelefono != null && !resultados.contains(porTelefono)) {
                resultados.add(porTelefono);
            }
            listaClientes.setAll(resultados);
        }
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
        cargarClientes();
    }

    @FXML
    private void cancelarEdicion() {
        limpiarFormulario();
        panelEdicion.setExpanded(false);
        tablaClientes.getSelectionModel().clearSelection();
        clienteSeleccionado = null;
        modoEdicion = false;
        btnEliminar.setVisible(false);
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtTelefono.clear();
        txtNotas.clear();
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }
}