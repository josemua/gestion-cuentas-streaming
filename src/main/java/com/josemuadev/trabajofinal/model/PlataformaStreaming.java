package com.josemuadev.trabajofinal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlataformaStreaming implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String notas;
    private List<CuentaStreaming> cuentas;

    public PlataformaStreaming() {
        this.cuentas = new ArrayList<>();
    }

    public PlataformaStreaming(String id, String nombre, String notas) {
        this.id = id;
        this.nombre = nombre;
        this.notas = notas;
        this.cuentas = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<CuentaStreaming> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentaStreaming> cuentas) {
        this.cuentas = cuentas;
    }

    public void agregarCuenta(CuentaStreaming cuenta) {
        this.cuentas.add(cuenta);
        cuenta.setPlataforma(this);
    }

    public void removerCuenta(CuentaStreaming cuenta) {
        this.cuentas.remove(cuenta);
        cuenta.setPlataforma(null);
    }

    public boolean tieneCuentasAsociadas() {
        return !cuentas.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlataformaStreaming that = (PlataformaStreaming) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
