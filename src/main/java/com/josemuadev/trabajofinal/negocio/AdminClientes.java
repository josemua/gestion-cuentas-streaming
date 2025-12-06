package com.josemuadev.trabajofinal.negocio;

import com.josemuadev.trabajofinal.model.Cliente;
import com.josemuadev.trabajofinal.model.EspacioCompartido;
import com.josemuadev.trabajofinal.util.GeneradorId;
import com.josemuadev.trabajofinal.util.StreamingException;
import com.josemuadev.trabajofinal.util.Validaciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminClientes {

    private Map<String, Cliente> clientes;

    public AdminClientes() {
        this.clientes = new HashMap<>();
    }

    public Cliente agregarCliente(String nombre, String telefono, String notas) throws StreamingException {
        Validaciones.validarTextoNoVacio(nombre, "Nombre");
        Validaciones.validarTelefono(telefono);

        for (Cliente c : clientes.values()) {
            if (c.getTelefono().equals(telefono)) {
                throw new StreamingException("Ya existe un cliente con el teléfono: " + telefono);
            }
        }

        String id = GeneradorId.generarId("CLI");
        Cliente cliente = new Cliente(id, nombre.trim(), telefono.trim(), notas);
        clientes.put(id, cliente);
        return cliente;
    }

    public void editarCliente(String id, String nombre, String telefono, String notas) throws StreamingException {
        Cliente cliente = buscarClientePorId(id);
        if (cliente == null) {
            throw new StreamingException("Cliente no encontrado con ID: " + id);
        }

        Validaciones.validarTextoNoVacio(nombre, "Nombre");
        Validaciones.validarTelefono(telefono);

        for (Cliente c : clientes.values()) {
            if (!c.getId().equals(id) && c.getTelefono().equals(telefono)) {
                throw new StreamingException("Ya existe otro cliente con el teléfono: " + telefono);
            }
        }

        cliente.setNombre(nombre.trim());
        cliente.setTelefono(telefono.trim());
        cliente.setNotas(notas);
    }

    public void eliminarCliente(String id) throws StreamingException {
        Cliente cliente = buscarClientePorId(id);
        if (cliente == null) {
            throw new StreamingException("Cliente no encontrado con ID: " + id);
        }

        if (cliente.tieneEspaciosActivos()) {
            throw new StreamingException("No se puede eliminar el cliente porque tiene espacios activos.");
        }

        if (cliente.tienePagosPendientes()) {
            throw new StreamingException("No se puede eliminar el cliente porque tiene pagos pendientes.");
        }

        clientes.remove(id);
    }

    public Cliente buscarClientePorId(String id) {
        return clientes.get(id);
    }

    public List<Cliente> buscarClientesPorNombre(String nombre) {
        String busqueda = nombre.toLowerCase();
        return clientes.values().stream()
                .filter(c -> c.getNombre().toLowerCase().contains(busqueda))
                .collect(Collectors.toList());
    }

    public Cliente buscarClientePorTelefono(String telefono) {
        return clientes.values().stream()
                .filter(c -> c.getTelefono().equals(telefono))
                .findFirst()
                .orElse(null);
    }

    public List<Cliente> listarClientes() {
        return new ArrayList<>(clientes.values());
    }

    public void cargarClientes(List<Cliente> listaClientes) {
        clientes.clear();
        for (Cliente c : listaClientes) {
            clientes.put(c.getId(), c);
        }
    }

    public int getTotalClientes() {
        return clientes.size();
    }
}
