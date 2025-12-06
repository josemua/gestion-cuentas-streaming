package com.josemuadev.trabajofinal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CuentaStreaming implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String email;
    private String password;
    private String plan;
    private int maxEspacios;
    private String notas;
    private PlataformaStreaming plataforma;
    private List<EspacioCompartido> espacios;

    public CuentaStreaming() {
        this.espacios = new ArrayList<>();
    }

    public CuentaStreaming(String id, String email, String password, String plan, int maxEspacios, String notas) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.plan = plan;
        this.maxEspacios = maxEspacios;
        this.notas = notas;
        this.espacios = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public int getMaxEspacios() {
        return maxEspacios;
    }

    public void setMaxEspacios(int maxEspacios) {
        this.maxEspacios = maxEspacios;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public PlataformaStreaming getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(PlataformaStreaming plataforma) {
        this.plataforma = plataforma;
    }

    public List<EspacioCompartido> getEspacios() {
        return espacios;
    }

    public void setEspacios(List<EspacioCompartido> espacios) {
        this.espacios = espacios;
    }

    public void agregarEspacio(EspacioCompartido espacio) {
        this.espacios.add(espacio);
        espacio.setCuenta(this);
    }

    public void removerEspacio(EspacioCompartido espacio) {
        this.espacios.remove(espacio);
        espacio.setCuenta(null);
    }

    public int getEspaciosDisponibles() {
        return maxEspacios - espacios.size();
    }

    public int getEspaciosOcupados() {
        return espacios.size();
    }

    public boolean tieneEspaciosDisponibles() {
        return getEspaciosDisponibles() > 0;
    }

    public boolean tieneEspaciosOcupados() {
        return !espacios.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuentaStreaming that = (CuentaStreaming) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String plat = plataforma != null ? plataforma.getNombre() : "Sin plataforma";
        return email + " (" + plat + " - " + plan + ")";
    }
}