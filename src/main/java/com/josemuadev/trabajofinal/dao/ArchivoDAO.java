package com.josemuadev.trabajofinal.dao;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoDAO<T extends Serializable> {

    private final Path rutaArchivo;
    private final String nombreEntidad;

    public ArchivoDAO(String nombreArchivo, String nombreEntidad) {
        Path directorioData = Paths.get(System.getProperty("user.home"), ".streaming-app", "data");
        try {
            Files.createDirectories(directorioData);
        } catch (IOException e) {
            System.err.println("Error creando directorio de datos: " + e.getMessage());
        }
        this.rutaArchivo = directorioData.resolve(nombreArchivo);
        this.nombreEntidad = nombreEntidad;
    }

    public void guardar(List<T> lista) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(rutaArchivo,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING))) {
            oos.writeObject(lista);
            System.out.println(nombreEntidad + " guardados: " + lista.size() + " registros");
        } catch (IOException e) {
            throw new IOException("Error al guardar " + nombreEntidad + ": " + e.getMessage(), e);
        }
    }

    public List<T> cargar() throws IOException {
        if (!Files.exists(rutaArchivo)) {
            System.out.println("Archivo de " + nombreEntidad + " no existe, retornando lista vacía.");
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(rutaArchivo))) {
            List<T> lista = (List<T>) ois.readObject();
            System.out.println(nombreEntidad + " cargados: " + lista.size() + " registros");
            return lista;
        } catch (ClassNotFoundException e) {
            throw new IOException("Error de formato en archivo de " + nombreEntidad, e);
        } catch (IOException e) {
            System.err.println("Archivo corrupto o incompatible, retornando lista vacía.");
            return new ArrayList<>();
        }
    }

    public boolean existeArchivo() {
        return Files.exists(rutaArchivo);
    }

    public boolean eliminarArchivo() throws IOException {
        return Files.deleteIfExists(rutaArchivo);
    }

    public String getRutaArchivo() {
        return rutaArchivo.toString();
    }
}
