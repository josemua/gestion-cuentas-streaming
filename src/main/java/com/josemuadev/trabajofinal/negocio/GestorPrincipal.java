package com.josemuadev.trabajofinal.negocio;

import com.josemuadev.trabajofinal.dao.PersistenciaManager;
import com.josemuadev.trabajofinal.model.*;
import com.josemuadev.trabajofinal.util.StreamingException;

import java.io.IOException;
import java.util.List;

public class GestorPrincipal {

    private static GestorPrincipal instancia;

    private final AdminClientes adminClientes;
    private final AdminPlataformas adminPlataformas;
    private final AdminCuentas adminCuentas;
    private final AdminEspacios adminEspacios;
    private final PersistenciaManager persistencia;

    private GestorPrincipal() {
        adminClientes = new AdminClientes();
        adminPlataformas = new AdminPlataformas();
        adminCuentas = new AdminCuentas();
        adminEspacios = new AdminEspacios();
        persistencia = PersistenciaManager.getInstance();
    }

    public static synchronized GestorPrincipal getInstance() {
        if (instancia == null) {
            instancia = new GestorPrincipal();
        }
        return instancia;
    }

    public AdminClientes getAdminClientes() {
        return adminClientes;
    }

    public AdminPlataformas getAdminPlataformas() {
        return adminPlataformas;
    }

    public AdminCuentas getAdminCuentas() {
        return adminCuentas;
    }

    public AdminEspacios getAdminEspacios() {
        return adminEspacios;
    }

    public void cargarDatos() throws IOException {
        System.out.println("Cargando datos del sistema...");

        List<PlataformaStreaming> plataformas = persistencia.cargarPlataformas();
        adminPlataformas.cargarPlataformas(plataformas);

        List<CuentaStreaming> cuentas = persistencia.cargarCuentas();
        adminCuentas.cargarCuentas(cuentas);

        List<Cliente> clientes = persistencia.cargarClientes();
        adminClientes.cargarClientes(clientes);

        List<EspacioCompartido> espacios = persistencia.cargarEspacios();
        adminEspacios.cargarEspacios(espacios);

        reconstruirRelaciones();

        System.out.println("Datos cargados correctamente.");
    }

    public void guardarDatos() throws IOException {
        System.out.println("Guardando datos del sistema...");

        persistencia.guardarPlataformas(adminPlataformas.listarPlataformas());
        persistencia.guardarCuentas(adminCuentas.listarCuentas());
        persistencia.guardarClientes(adminClientes.listarClientes());
        persistencia.guardarEspacios(adminEspacios.listarEspacios());

        System.out.println("Datos guardados correctamente.");
    }

    private void reconstruirRelaciones() {
        System.out.println("Reconstruyendo relaciones bidireccionales...");

        List<PlataformaStreaming> plataformas = adminPlataformas.listarPlataformas();
        List<CuentaStreaming> cuentas = adminCuentas.listarCuentas();

        for (PlataformaStreaming plataforma : plataformas) {
            plataforma.getCuentas().clear();
        }

        for (CuentaStreaming cuenta : cuentas) {
            if (cuenta.getPlataforma() != null) {
                PlataformaStreaming plataforma = cuenta.getPlataforma();
                if (!plataforma.getCuentas().contains(cuenta)) {
                    plataforma.getCuentas().add(cuenta);
                }
            }
        }

        List<Cliente> clientes = adminClientes.listarClientes();
        List<EspacioCompartido> espacios = adminEspacios.listarEspacios();

        for (CuentaStreaming cuenta : cuentas) {
            cuenta.getEspacios().clear();
        }

        for (Cliente cliente : clientes) {
            cliente.getEspacios().clear();
        }

        for (EspacioCompartido espacio : espacios) {
            if (espacio.getCuenta() != null) {
                CuentaStreaming cuenta = espacio.getCuenta();
                if (!cuenta.getEspacios().contains(espacio)) {
                    cuenta.getEspacios().add(espacio);
                }
            }

            if (espacio.getCliente() != null) {
                Cliente cliente = espacio.getCliente();
                if (!cliente.getEspacios().contains(espacio)) {
                    cliente.getEspacios().add(espacio);
                }
            }

            for (RegistroPago pago : espacio.getPagos()) {
                if (pago.getEspacio() == null) {
                    pago.setEspacio(espacio);
                }
            }
        }

        System.out.println("Relaciones reconstruidas correctamente.");
    }

    public String getEstadisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADÍSTICAS DEL SISTEMA ===\n\n");
        sb.append("Clientes registrados: ").append(adminClientes.getTotalClientes()).append("\n");
        sb.append("Plataformas: ").append(adminPlataformas.getTotalPlataformas()).append("\n");
        sb.append("Cuentas de streaming: ").append(adminCuentas.getTotalCuentas()).append("\n");
        sb.append("Espacios ocupados: ").append(adminEspacios.getTotalEspacios()).append("\n");
        sb.append("Espacios disponibles: ").append(adminCuentas.getTotalEspaciosDisponibles()).append("\n");
        sb.append("Deuda total: $").append(String.format("%.2f", adminEspacios.getDeudaTotal())).append("\n");
        return sb.toString();
    }

    public void cargarDatosEjemplo() throws StreamingException {
        PlataformaStreaming netflix = adminPlataformas.agregarPlataforma("Netflix", "Streaming de series y películas");
        PlataformaStreaming disney = adminPlataformas.agregarPlataforma("Disney+", "Contenido Disney, Marvel, Star Wars");
        PlataformaStreaming amazon = adminPlataformas.agregarPlataforma("Amazon Prime Video", "Streaming + envíos gratis");
        PlataformaStreaming hbo = adminPlataformas.agregarPlataforma("HBO Max", "Series y películas premium");
        PlataformaStreaming spotify = adminPlataformas.agregarPlataforma("Spotify Premium", "Música sin anuncios");

        CuentaStreaming cuentaNetflix1 = adminCuentas.agregarCuenta(
                "familia.rodriguez@gmail.com", "Netflix2024*", "Premium - 4 pantallas", 5, "Cuenta familiar principal", netflix);
        CuentaStreaming cuentaNetflix2 = adminCuentas.agregarCuenta(
                "netflix.grupal@hotmail.com", "Pass1234", "Estándar - 2 pantallas", 4, "Cuenta grupal secundaria", netflix);

        CuentaStreaming cuentaDisney = adminCuentas.agregarCuenta(
                "disney.familia@gmail.com", "DisneyPass2024", "Premium - 4 dispositivos", 4, "Contenido infantil y Marvel", disney);

        CuentaStreaming cuentaAmazon = adminCuentas.agregarCuenta(
                "prime.stream@outlook.com", "AmazonPrime123", "Plan Anual", 3, "Incluye Prime Shopping", amazon);

        CuentaStreaming cuentaHBO = adminCuentas.agregarCuenta(
                "hbo.max@yahoo.com", "HBOMax2024!", "Premium - 3 pantallas", 6, "Series GOT, Friends, etc", hbo);

        CuentaStreaming cuentaSpotify = adminCuentas.agregarCuenta(
                "spotify.familia@gmail.com", "MusicPass456", "Familiar", 6, "Plan familiar premium", spotify);

        Cliente cliente1 = adminClientes.agregarCliente("Simón Bólivar", "3001234567", "El libertador. Moroso recurrente");
        Cliente cliente2 = adminClientes.agregarCliente("Santander", "3109876543", "Se le cobra doble");
        Cliente cliente3 = adminClientes.agregarCliente("Antonio Nariño", "3201112233", "Paga puntual");
        Cliente cliente4 = adminClientes.agregarCliente("Juan José Rondón", "3164445566", "Coronel");
        Cliente cliente5 = adminClientes.agregarCliente("Francisco de Caldas", "3174445566", "El sabio");
        Cliente cliente6 = adminClientes.agregarCliente("Miranda", "3154445566", "INACTIVO - no contesta");
        Cliente cliente7 = adminClientes.agregarCliente("Manuelita", "3104445566", "La patrona");
        Cliente cliente8 = adminClientes.agregarCliente("José María Córdova", "3187778899", "El héroe de Ayacucho");
        Cliente cliente9 = adminClientes.agregarCliente("Policarpa Salavarrieta", "3195556677", "La Pola - paga adelantado");
        Cliente cliente10 = adminClientes.agregarCliente("Camilo Torres", "3023334455", "El sabio Caldas lo recomendó");

        EspacioCompartido e1 = adminEspacios.crearEspacio(cliente1, cuentaNetflix1, 7000, "El libertador");
        EspacioCompartido e2 = adminEspacios.crearEspacio(cliente2, cuentaNetflix1, 15000, "Santander (doble tarifa)");
        EspacioCompartido e3 = adminEspacios.crearEspacio(cliente3, cuentaNetflix1, 7000, "Toño");
        EspacioCompartido e4 = adminEspacios.crearEspacio(cliente4, cuentaNetflix1, 7000, "Coronel Rondón");

        EspacioCompartido e5 = adminEspacios.crearEspacio(cliente5, cuentaNetflix2, 6500, "El sabio Caldas");
        EspacioCompartido e6 = adminEspacios.crearEspacio(cliente7, cuentaNetflix2, 7000, "La patrona");
        EspacioCompartido e7 = adminEspacios.crearEspacio(cliente8, cuentaNetflix2, 6500, "Héroe de Ayacucho");

        EspacioCompartido e8 = adminEspacios.crearEspacio(cliente3, cuentaDisney, 6000, "Toño - contenido familiar");
        EspacioCompartido e9 = adminEspacios.crearEspacio(cliente7, cuentaDisney, 6500, "Manuelita y familia");
        EspacioCompartido e10 = adminEspacios.crearEspacio(cliente9, cuentaDisney, 6000, "La Pola");

        EspacioCompartido e11 = adminEspacios.crearEspacio(cliente4, cuentaAmazon, 9000, "Coronel - con envíos");
        EspacioCompartido e12 = adminEspacios.crearEspacio(cliente10, cuentaAmazon, 9000, "Camilo Torres");

        EspacioCompartido e13 = adminEspacios.crearEspacio(cliente1, cuentaHBO, 7500, "Bolívar ve GOT");
        EspacioCompartido e14 = adminEspacios.crearEspacio(cliente2, cuentaHBO, 15000, "Santander HBO (doble)");
        EspacioCompartido e15 = adminEspacios.crearEspacio(cliente5, cuentaHBO, 7000, "Caldas - documentales");
        EspacioCompartido e16 = adminEspacios.crearEspacio(cliente8, cuentaHBO, 7000, "Córdova - series bélicas");
        EspacioCompartido e17 = adminEspacios.crearEspacio(cliente9, cuentaHBO, 6500, "La Pola");

        EspacioCompartido e18 = adminEspacios.crearEspacio(cliente3, cuentaSpotify, 4500, "Toño música");
        EspacioCompartido e19 = adminEspacios.crearEspacio(cliente5, cuentaSpotify, 4500, "Caldas - clásica");
        EspacioCompartido e20 = adminEspacios.crearEspacio(cliente7, cuentaSpotify, 4500, "Manuelita playlist");
        EspacioCompartido e21 = adminEspacios.crearEspacio(cliente10, cuentaSpotify, 4500, "Camilo Torres");

        adminEspacios.registrarPago(e3.getId(), "2025-10", 7000, true);
        adminEspacios.registrarPago(e3.getId(), "2025-11", 7000, true);
        adminEspacios.registrarPago(e3.getId(), "2025-12", 7000, true);
        adminEspacios.registrarPago(e8.getId(), "2025-11", 6000, true);
        adminEspacios.registrarPago(e8.getId(), "2025-12", 6000, true);
        adminEspacios.registrarPago(e18.getId(), "2025-12", 4500, true);
        adminEspacios.registrarPago(e1.getId(), "2025-10", 7000, true);
        adminEspacios.registrarPago(e1.getId(), "2025-11", 7000, true);
        adminEspacios.registrarPago(e1.getId(), "2025-12", 7000, false);
        adminEspacios.registrarPago(e13.getId(), "2025-11", 7500, true);
        adminEspacios.registrarPago(e13.getId(), "2025-12", 7500, false);
        adminEspacios.registrarPago(e2.getId(), "2025-10", 15000, true);
        adminEspacios.registrarPago(e2.getId(), "2025-11", 15000, true);
        adminEspacios.registrarPago(e2.getId(), "2025-12", 15000, true);
        adminEspacios.registrarPago(e14.getId(), "2025-12", 15000, true);
        adminEspacios.registrarPago(e10.getId(), "2025-11", 6000, true);
        adminEspacios.registrarPago(e10.getId(), "2025-12", 6000, true);
        adminEspacios.registrarPago(e17.getId(), "2025-12", 6500, true);
        adminEspacios.registrarPago(e6.getId(), "2025-11", 7000, true);
        adminEspacios.registrarPago(e6.getId(), "2025-12", 7000, true);
        adminEspacios.registrarPago(e9.getId(), "2025-12", 6500, true);
        adminEspacios.registrarPago(e20.getId(), "2025-12", 4500, true);
        adminEspacios.registrarPago(e4.getId(), "2025-12", 7000, true);
        adminEspacios.registrarPago(e5.getId(), "2025-12", 6500, false);
        adminEspacios.registrarPago(e11.getId(), "2025-12", 9000, true);

        System.out.println("Datos de ejemplo cargados correctamente.");
        System.out.println("Ocupación total: 21/32 espacios (65%)");
        System.out.println("- Netflix: 7/9 espacios (78%)");
        System.out.println("- Disney+: 3/4 espacios (75%)");
        System.out.println("- Amazon: 2/3 espacios (67%)");
        System.out.println("- HBO Max: 5/6 espacios (83%)");
        System.out.println("- Spotify: 4/6 espacios (67%)");
    }
}
