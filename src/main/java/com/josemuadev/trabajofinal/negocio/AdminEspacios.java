package com.josemuadev.trabajofinal.negocio;

import com.josemuadev.trabajofinal.model.*;
import com.josemuadev.trabajofinal.util.GeneradorId;
import com.josemuadev.trabajofinal.util.StreamingException;
import com.josemuadev.trabajofinal.util.Validaciones;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminEspacios {

    private List<EspacioCompartido> espacios;

    public AdminEspacios() {
        this.espacios = new ArrayList<>();
    }

    public EspacioCompartido crearEspacio(Cliente cliente, CuentaStreaming cuenta,
                                          double precioMensual, String notas) throws StreamingException {
        if (cliente == null) {
            throw new StreamingException("Debe seleccionar un cliente.");
        }
        if (cuenta == null) {
            throw new StreamingException("Debe seleccionar una cuenta.");
        }
        Validaciones.validarPrecioPositivo(precioMensual, "Precio mensual");

        if (!cuenta.tieneEspaciosDisponibles()) {
            throw new StreamingException("La cuenta no tiene espacios disponibles. " +
                    "Máximo: " + cuenta.getMaxEspacios() + ", Ocupados: " + cuenta.getEspaciosOcupados());
        }

        String id = GeneradorId.generarId("ESP");
        EspacioCompartido espacio = new EspacioCompartido(id, precioMensual, LocalDate.now(), notas);

        espacio.setCliente(cliente);
        espacio.setCuenta(cuenta);
        cliente.agregarEspacio(espacio);
        cuenta.agregarEspacio(espacio);

        espacios.add(espacio);
        return espacio;
    }

    public void editarEspacio(String id, double precioMensual, String notas) throws StreamingException {
        EspacioCompartido espacio = buscarEspacioPorId(id);
        if (espacio == null) {
            throw new StreamingException("Espacio no encontrado con ID: " + id);
        }

        Validaciones.validarPrecioPositivo(precioMensual, "Precio mensual");

        espacio.setPrecioMensual(precioMensual);
        espacio.setNotas(notas);
    }

    public void eliminarEspacio(String id) throws StreamingException {
        EspacioCompartido espacio = buscarEspacioPorId(id);
        if (espacio == null) {
            throw new StreamingException("Espacio no encontrado con ID: " + id);
        }

        if (espacio.getCliente() != null) {
            espacio.getCliente().removerEspacio(espacio);
        }
        if (espacio.getCuenta() != null) {
            espacio.getCuenta().removerEspacio(espacio);
        }

        espacios.remove(espacio);
    }

    public RegistroPago registrarPago(String espacioId, String mes, double monto,
                                      boolean pagado) throws StreamingException {
        EspacioCompartido espacio = buscarEspacioPorId(espacioId);
        if (espacio == null) {
            throw new StreamingException("Espacio no encontrado con ID: " + espacioId);
        }

        Validaciones.validarFormatoMes(mes);
        Validaciones.validarPrecioPositivo(monto, "Monto del pago");

        for (RegistroPago pago : espacio.getPagos()) {
            if (pago.getMes().equals(mes)) {
                throw new StreamingException("Ya existe un registro de pago para el mes: " + mes);
            }
        }

        String id = GeneradorId.generarId("PAG");
        RegistroPago pago = new RegistroPago(id, mes, monto, pagado);
        espacio.agregarPago(pago);
        return pago;
    }

    public void marcarPagado(String espacioId, String pagoId) throws StreamingException {
        EspacioCompartido espacio = buscarEspacioPorId(espacioId);
        if (espacio == null) {
            throw new StreamingException("Espacio no encontrado.");
        }

        RegistroPago pago = espacio.getPagos().stream()
                .filter(p -> p.getId().equals(pagoId))
                .findFirst()
                .orElse(null);

        if (pago == null) {
            throw new StreamingException("Pago no encontrado.");
        }

        pago.setPagado(true);
        pago.setFechaPago(LocalDate.now());
    }

    public int generarPagosMensuales() {
        String mesActual = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        int pagosGenerados = 0;

        for (EspacioCompartido espacio : espacios) {
            boolean existePago = espacio.getPagos().stream()
                    .anyMatch(p -> p.getMes().equals(mesActual));

            if (!existePago) {
                try {
                    registrarPago(espacio.getId(), mesActual, espacio.getPrecioMensual(), false);
                    pagosGenerados++;
                } catch (StreamingException e) {
                    System.err.println("Error al generar pago para espacio " + espacio.getId() + ": " + e.getMessage());
                }
            }
        }

        return pagosGenerados;
    }

    public EspacioCompartido buscarEspacioPorId(String id) {
        return espacios.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<EspacioCompartido> listarEspaciosPorCliente(Cliente cliente) {
        return espacios.stream()
                .filter(e -> e.getCliente() != null && e.getCliente().equals(cliente))
                .collect(Collectors.toList());
    }

    public List<EspacioCompartido> listarEspaciosPorCuenta(CuentaStreaming cuenta) {
        return espacios.stream()
                .filter(e -> e.getCuenta() != null && e.getCuenta().equals(cuenta))
                .collect(Collectors.toList());
    }

    public List<EspacioCompartido> listarEspaciosConDeudas() {
        return espacios.stream()
                .filter(e -> e.getPagosPendientes() > 0)
                .collect(Collectors.toList());
    }

    public List<EspacioCompartido> listarEspacios() {
        return new ArrayList<>(espacios);
    }

    public void cargarEspacios(List<EspacioCompartido> listaEspacios) {
        this.espacios = new ArrayList<>(listaEspacios);
    }

    public int getTotalEspacios() {
        return espacios.size();
    }

    public double getDeudaTotal() {
        return espacios.stream()
                .mapToDouble(EspacioCompartido::getDeudaTotal)
                .sum();
    }
}
