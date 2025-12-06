package com.josemuadev.trabajofinal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private String telefono;
    private String notas;
    private List<EspacioCompartido> espacios;

    public Cliente() {
        this.espacios = new ArrayList<>();
    }

    public Cliente(String id, String nombre, String telefono, String notas) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.notas = notas;
        this.espacios = new ArrayList<>();
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<EspacioCompartido> getEspacios() {
        return espacios;
    }

    public void setEspacios(List<EspacioCompartido> espacios) {
        this.espacios = espacios;
    }

    public void agregarEspacio(EspacioCompartido espacio) {
        this.espacios.add(espacio);
    }

    public void removerEspacio(EspacioCompartido espacio) {
        this.espacios.remove(espacio);
        espacio.setCliente(null);
    }

    public boolean tieneEspaciosActivos() {
        return !espacios.isEmpty();
    }

    public boolean tienePagosPendientes() {
        for (EspacioCompartido espacio : espacios) {
            for (RegistroPago pago : espacio.getPagos()) {
                if (!pago.isPagado()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre + " (" + telefono + ")";
    }
}
