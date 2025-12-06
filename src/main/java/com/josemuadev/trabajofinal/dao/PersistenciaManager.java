package com.josemuadev.trabajofinal.dao;

import com.josemuadev.trabajofinal.model.*;
import java.io.IOException;
import java.util.List;

public class PersistenciaManager {

    private static PersistenciaManager instancia;

    private final ArchivoDAO<Cliente> clienteDAO;
    private final ArchivoDAO<PlataformaStreaming> plataformaDAO;
    private final ArchivoDAO<CuentaStreaming> cuentaDAO;
    private final ArchivoDAO<EspacioCompartido> espacioDAO;

    private PersistenciaManager() {
        clienteDAO = new ArchivoDAO<>("clientes.dat", "Clientes");
        plataformaDAO = new ArchivoDAO<>("plataformas.dat", "Plataformas");
        cuentaDAO = new ArchivoDAO<>("cuentas.dat", "Cuentas");
        espacioDAO = new ArchivoDAO<>("espacios.dat", "Espacios");
    }

    public static synchronized PersistenciaManager getInstance() {
        if (instancia == null) {
            instancia = new PersistenciaManager();
        }
        return instancia;
    }

    public void guardarClientes(List<Cliente> clientes) throws IOException {
        clienteDAO.guardar(clientes);
    }

    public List<Cliente> cargarClientes() throws IOException {
        return clienteDAO.cargar();
    }

    public void guardarPlataformas(List<PlataformaStreaming> plataformas) throws IOException {
        plataformaDAO.guardar(plataformas);
    }

    public List<PlataformaStreaming> cargarPlataformas() throws IOException {
        return plataformaDAO.cargar();
    }

    public void guardarCuentas(List<CuentaStreaming> cuentas) throws IOException {
        cuentaDAO.guardar(cuentas);
    }

    public List<CuentaStreaming> cargarCuentas() throws IOException {
        return cuentaDAO.cargar();
    }

    public void guardarEspacios(List<EspacioCompartido> espacios) throws IOException {
        espacioDAO.guardar(espacios);
    }

    public List<EspacioCompartido> cargarEspacios() throws IOException {
        return espacioDAO.cargar();
    }

    public void guardarTodo(List<Cliente> clientes,
                            List<PlataformaStreaming> plataformas,
                            List<CuentaStreaming> cuentas,
                            List<EspacioCompartido> espacios) throws IOException {
        guardarClientes(clientes);
        guardarPlataformas(plataformas);
        guardarCuentas(cuentas);
        guardarEspacios(espacios);
    }

    public String getInfoAlmacenamiento() {
        return "Directorio de datos: " + System.getProperty("user.home") +
                "/.streaming-app/data/";
    }
}
