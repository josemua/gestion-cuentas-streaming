package com.josemuadev.trabajofinal.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EspacioCompartido implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private double precioMensual;
    private LocalDate fechaInicio;
    private String notas;
    private Cliente cliente;
    private CuentaStreaming cuenta;
    private List<RegistroPago> pagos;

    public EspacioCompartido() {
        this.pagos = new ArrayList<>();
        this.fechaInicio = LocalDate.now();
    }

    public EspacioCompartido(String id, double precioMensual, LocalDate fechaInicio, String notas) {
        this.id = id;
        this.precioMensual = precioMensual;
        this.fechaInicio = fechaInicio;
        this.notas = notas;
        this.pagos = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(double precioMensual) {
        this.precioMensual = precioMensual;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public CuentaStreaming getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaStreaming cuenta) {
        this.cuenta = cuenta;
    }

    public List<RegistroPago> getPagos() {
        return pagos;
    }

    public void setPagos(List<RegistroPago> pagos) {
        this.pagos = pagos;
    }

    public void agregarPago(RegistroPago pago) {
        this.pagos.add(pago);
        pago.setEspacio(this);
    }

    public void removerPago(RegistroPago pago) {
        this.pagos.remove(pago);
    }

    public int getPagosPendientes() {
        int pendientes = 0;
        for (RegistroPago pago : pagos) {
            if (!pago.isPagado()) {
                pendientes++;
            }
        }
        return pendientes;
    }

    public double getDeudaTotal() {
        double deuda = 0;
        for (RegistroPago pago : pagos) {
            if (!pago.isPagado()) {
                deuda += pago.getMonto();
            }
        }
        return deuda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EspacioCompartido that = (EspacioCompartido) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String clienteNombre = cliente != null ? cliente.getNombre() : "Sin cliente";
        String cuentaEmail = cuenta != null ? cuenta.getEmail() : "Sin cuenta";
        return "Espacio " + id + " - " + clienteNombre + " en " + cuentaEmail;
    }
}