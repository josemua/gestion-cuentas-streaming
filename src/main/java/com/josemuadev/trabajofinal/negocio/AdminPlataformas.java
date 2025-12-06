package com.josemuadev.trabajofinal.negocio;

import com.josemuadev.trabajofinal.model.PlataformaStreaming;
import com.josemuadev.trabajofinal.util.GeneradorId;
import com.josemuadev.trabajofinal.util.StreamingException;
import com.josemuadev.trabajofinal.util.Validaciones;

import java.util.ArrayList;
import java.util.List;

public class AdminPlataformas {

    private List<PlataformaStreaming> plataformas;

    public AdminPlataformas() {
        this.plataformas = new ArrayList<>();
    }

    public PlataformaStreaming agregarPlataforma(String nombre, String notas) throws StreamingException {
        Validaciones.validarTextoNoVacio(nombre, "Nombre de plataforma");

        for (PlataformaStreaming p : plataformas) {
            if (p.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new StreamingException("Ya existe una plataforma con el nombre: " + nombre);
            }
        }

        String id = GeneradorId.generarId("PLAT");
        PlataformaStreaming plataforma = new PlataformaStreaming(id, nombre.trim(), notas);
        plataformas.add(plataforma);
        return plataforma;
    }

    public void editarPlataforma(String id, String nombre, String notas) throws StreamingException {
        PlataformaStreaming plataforma = buscarPlataformaPorId(id);
        if (plataforma == null) {
            throw new StreamingException("Plataforma no encontrada con ID: " + id);
        }

        Validaciones.validarTextoNoVacio(nombre, "Nombre de plataforma");

        for (PlataformaStreaming p : plataformas) {
            if (!p.getId().equals(id) && p.getNombre().equalsIgnoreCase(nombre.trim())) {
                throw new StreamingException("Ya existe otra plataforma con el nombre: " + nombre);
            }
        }

        plataforma.setNombre(nombre.trim());
        plataforma.setNotas(notas);
    }

    public void eliminarPlataforma(String id) throws StreamingException {
        PlataformaStreaming plataforma = buscarPlataformaPorId(id);
        if (plataforma == null) {
            throw new StreamingException("Plataforma no encontrada con ID: " + id);
        }

        if (plataforma.tieneCuentasAsociadas()) {
            throw new StreamingException("No se puede eliminar la plataforma porque tiene cuentas asociadas.");
        }

        plataformas.remove(plataforma);
    }

    public PlataformaStreaming buscarPlataformaPorId(String id) {
        return plataformas.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public PlataformaStreaming buscarPlataformaPorNombre(String nombre) {
        return plataformas.stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    public List<PlataformaStreaming> listarPlataformas() {
        return new ArrayList<>(plataformas);
    }

    public void cargarPlataformas(List<PlataformaStreaming> listaPlataformas) {
        this.plataformas = new ArrayList<>(listaPlataformas);
    }

    public int getTotalPlataformas() {
        return plataformas.size();
    }
}
