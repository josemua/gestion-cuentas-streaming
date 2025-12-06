package com.josemuadev.trabajofinal.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class RegistroPago implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String mes;
    private double monto;
    private boolean pagado;
    private LocalDate fechaPago;
    private EspacioCompartido espacio;

    public RegistroPago() {
    }

    public RegistroPago(String id, String mes, double monto, boolean pagado) {
        this.id = id;
        this.mes = mes;
        this.monto = monto;
        this.pagado = pagado;
        if (pagado) {
            this.fechaPago = LocalDate.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
        if (pagado && fechaPago == null) {
            this.fechaPago = LocalDate.now();
        }
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public EspacioCompartido getEspacio() {
        return espacio;
    }

    public void setEspacio(EspacioCompartido espacio) {
        this.espacio = espacio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistroPago that = (RegistroPago) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String estado = pagado ? "Pagado" : "Pendiente";
        return mes + " - $" + monto + " (" + estado + ")";
    }
}