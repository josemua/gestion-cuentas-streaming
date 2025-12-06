package com.josemuadev.trabajofinal.controller;

import com.josemuadev.trabajofinal.model.EspacioCompartido;
import com.josemuadev.trabajofinal.model.RegistroPago;
import com.josemuadev.trabajofinal.negocio.AdminEspacios;
import com.josemuadev.trabajofinal.negocio.GestorPrincipal;
import com.josemuadev.trabajofinal.util.StreamingException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.geometry.Pos;

import java.net.URL;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PagosController implements Initializable {

    public static class PagoView {
        private final RegistroPago pago;
        private final EspacioCompartido espacio;

        public PagoView(RegistroPago pago, EspacioCompartido espacio) {
            this.pago = pago;
            this.espacio = espacio;
        }

        public RegistroPago getPago() { return pago; }
        public EspacioCompartido getEspacio() { return espacio; }
    }

    @FXML private TableView<PagoView> tablaPagos;
    @FXML private TableColumn<PagoView, String> colMes;
    @FXML private TableColumn<PagoView, String> colCliente;
    @FXML private TableColumn<PagoView, String> colCuenta;
    @FXML private TableColumn<PagoView, String> colMonto;
    @FXML private TableColumn<PagoView, String> colEstado;
    @FXML private TableColumn<PagoView, String> colFechaPago;
    @FXML private TableColumn<PagoView, Void> colAcciones;

    @FXML private ComboBox<EspacioCompartido> cmbFiltroEspacio;
    @FXML private CheckBox chkSoloPendientes;
    @FXML private Label lblDeudaTotal;
    @FXML private Label lblPagosPendientes;

    @FXML private TitledPane panelNuevoPago;
    @FXML private ComboBox<EspacioCompartido> cmbEspacio;
    @FXML private TextField txtMes;
    @FXML private TextField txtMonto;
    @FXML private CheckBox chkPagado;
    @FXML private Label lblMensaje;

    private AdminEspacios adminEspacios;
    private ObservableList<PagoView> listaPagos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminEspacios = GestorPrincipal.getInstance().getAdminEspacios();
        listaPagos = FXCollections.observableArrayList();

        configurarTabla();
        cargarCombos();
        cargarPagos();
        actualizarResumen();

        txtMes.setText(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
    }

    private void configurarTabla() {
        colMes.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPago().getMes()));

        colCliente.setCellValueFactory(cellData -> {
            EspacioCompartido esp = cellData.getValue().getEspacio();
            String nombre = esp.getCliente() != null ? esp.getCliente().getNombre() : "-";
            return new SimpleStringProperty(nombre);
        });

        colCuenta.setCellValueFactory(cellData -> {
            EspacioCompartido esp = cellData.getValue().getEspacio();
            String email = esp.getCuenta() != null ? esp.getCuenta().getEmail() : "-";
            return new SimpleStringProperty(email);
        });

        colMonto.setCellValueFactory(cellData ->
                new SimpleStringProperty("$" + String.format("%.0f", cellData.getValue().getPago().getMonto())));

        colEstado.setCellValueFactory(cellData -> {
            boolean pagado = cellData.getValue().getPago().isPagado();
            return new SimpleStringProperty(pagado ? "✅ Pagado" : "⏳ Pendiente");
        });

        colFechaPago.setCellValueFactory(cellData -> {
            RegistroPago pago = cellData.getValue().getPago();
            if (pago.getFechaPago() != null) {
                return new SimpleStringProperty(pago.getFechaPago().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            return new SimpleStringProperty("-");
        });

        colAcciones.setCellFactory(col -> new TableCell<PagoView, Void>() {
            private final Button btnPagar = new Button("💰 Pagar");
            {
                btnPagar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px;");
                btnPagar.setOnAction(e -> {
                    PagoView pagoView = getTableView().getItems().get(getIndex());
                    marcarComoPagado(pagoView);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PagoView pagoView = getTableView().getItems().get(getIndex());
                    if (!pagoView.getPago().isPagado()) {
                        setGraphic(btnPagar);
                        setAlignment(Pos.CENTER);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void marcarComoPagado(PagoView pagoView) {
        try {
            adminEspacios.marcarPagado(
                    pagoView.getEspacio().getId(),
                    pagoView.getPago().getId()
            );
            GestorPrincipal.getInstance().guardarDatos();
            cargarPagos();
            actualizarResumen();
            mostrarMensaje("Pago registrado correctamente", false);
        } catch (StreamingException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (Exception e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), true);
        }
    }

    private void cargarCombos() {
        List<EspacioCompartido> espacios = adminEspacios.listarEspacios();

        cmbEspacio.setItems(FXCollections.observableArrayList(espacios));

        ObservableList<EspacioCompartido> filtroList = FXCollections.observableArrayList();
        filtroList.add(null);
        filtroList.addAll(espacios);
        cmbFiltroEspacio.setItems(filtroList);

        cmbFiltroEspacio.setButtonCell(new ListCell<EspacioCompartido>() {
            @Override
            protected void updateItem(EspacioCompartido item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? "Todos los espacios" : item.toString());
            }
        });
        cmbFiltroEspacio.setCellFactory(lv -> new ListCell<EspacioCompartido>() {
            @Override
            protected void updateItem(EspacioCompartido item, boolean empty) {
                super.updateItem(item, empty);
                setText(item == null ? "Todos los espacios" : item.toString());
            }
        });
    }

    private void cargarPagos() {
        listaPagos.clear();

        for (EspacioCompartido espacio : adminEspacios.listarEspacios()) {
            for (RegistroPago pago : espacio.getPagos()) {
                listaPagos.add(new PagoView(pago, espacio));
            }
        }

        listaPagos.sort((a, b) -> b.getPago().getMes().compareTo(a.getPago().getMes()));

        aplicarFiltros();
    }

    private void aplicarFiltros() {
        List<PagoView> filtrados = new ArrayList<>();

        EspacioCompartido espacioFiltro = cmbFiltroEspacio.getValue();
        boolean soloPendientes = chkSoloPendientes.isSelected();

        for (PagoView pv : listaPagos) {
            boolean incluir = true;

            if (espacioFiltro != null && !pv.getEspacio().equals(espacioFiltro)) {
                incluir = false;
            }

            if (soloPendientes && pv.getPago().isPagado()) {
                incluir = false;
            }

            if (incluir) {
                filtrados.add(pv);
            }
        }

        tablaPagos.setItems(FXCollections.observableArrayList(filtrados));
    }

    private void actualizarResumen() {
        double deudaTotal = adminEspacios.getDeudaTotal();
        lblDeudaTotal.setText("$" + String.format("%.0f", deudaTotal));

        int pendientes = 0;
        for (EspacioCompartido esp : adminEspacios.listarEspacios()) {
            pendientes += esp.getPagosPendientes();
        }
        lblPagosPendientes.setText(String.valueOf(pendientes));
    }

    @FXML
    private void generarPagosMensuales() {
        try {
            int generados = adminEspacios.generarPagosMensuales();
            GestorPrincipal.getInstance().guardarDatos();
            cargarPagos();
            actualizarResumen();

            if (generados > 0) {
                mostrarMensaje("Se generaron " + generados + " pagos para el mes actual", false);
            } else {
                mostrarMensaje("No hay pagos nuevos para generar", false);
            }
        } catch (Exception e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), true);
        }
    }

    @FXML
    private void registrarPago() {
        try {
            EspacioCompartido espacio = cmbEspacio.getValue();
            if (espacio == null) {
                throw new StreamingException("Debe seleccionar un espacio.");
            }

            String mes = txtMes.getText().trim();

            double monto;
            try {
                monto = Double.parseDouble(txtMonto.getText().trim());
            } catch (NumberFormatException e) {
                // Usar precio del espacio si no se especifica
                if (txtMonto.getText().trim().isEmpty()) {
                    monto = espacio.getPrecioMensual();
                } else {
                    throw new StreamingException("El monto debe ser un número válido.");
                }
            }

            boolean pagado = chkPagado.isSelected();

            adminEspacios.registrarPago(espacio.getId(), mes, monto, pagado);
            GestorPrincipal.getInstance().guardarDatos();

            cargarPagos();
            actualizarResumen();
            cancelar();
            mostrarMensaje("Pago registrado correctamente", false);

        } catch (StreamingException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (Exception e) {
            mostrarMensaje("Error al guardar: " + e.getMessage(), true);
        }
    }

    @FXML
    private void filtrarPorEspacio() {
        aplicarFiltros();
    }

    @FXML
    private void filtrar() {
        aplicarFiltros();
    }

    @FXML
    private void limpiarFiltro() {
        cmbFiltroEspacio.setValue(null);
        chkSoloPendientes.setSelected(false);
        cargarPagos();
    }

    @FXML
    private void cancelar() {
        cmbEspacio.setValue(null);
        txtMes.setText(YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        txtMonto.clear();
        chkPagado.setSelected(false);
        panelNuevoPago.setExpanded(false);
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }
}