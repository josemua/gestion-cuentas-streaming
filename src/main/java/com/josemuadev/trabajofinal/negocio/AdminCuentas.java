package com.josemuadev.trabajofinal.negocio;

import com.josemuadev.trabajofinal.model.CuentaStreaming;
import com.josemuadev.trabajofinal.model.PlataformaStreaming;
import com.josemuadev.trabajofinal.util.GeneradorId;
import com.josemuadev.trabajofinal.util.StreamingException;
import com.josemuadev.trabajofinal.util.Validaciones;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminCuentas {

    private List<CuentaStreaming> cuentas;

    public AdminCuentas() {
        this.cuentas = new ArrayList<>();
    }

    public CuentaStreaming agregarCuenta(String email, String password, String plan,
                                         int maxEspacios, String notas,
                                         PlataformaStreaming plataforma) throws StreamingException {
        Validaciones.validarEmail(email);
        Validaciones.validarTextoNoVacio(password, "Contraseña");
        Validaciones.validarTextoNoVacio(plan, "Plan");
        Validaciones.validarNumeroPositivo(maxEspacios, "Número máximo de espacios");

        if (plataforma == null) {
            throw new StreamingException("Debe seleccionar una plataforma para la cuenta.");
        }

        for (CuentaStreaming c : cuentas) {
            if (c.getEmail().equalsIgnoreCase(email.trim()) &&
                c.getPlataforma() != null && c.getPlataforma().equals(plataforma)) {
                throw new StreamingException("Ya existe una cuenta con el email: " + email + " en la plataforma " + plataforma.getNombre());
            }
        }

        String id = GeneradorId.generarId("CTA");
        CuentaStreaming cuenta = new CuentaStreaming(id, email.trim(), password, plan.trim(), maxEspacios, notas);
        cuenta.setPlataforma(plataforma);
        plataforma.agregarCuenta(cuenta);
        cuentas.add(cuenta);
        return cuenta;
    }

    public void editarCuenta(String id, String email, String password, String plan,
                             int maxEspacios, String notas) throws StreamingException {
        CuentaStreaming cuenta = buscarCuentaPorId(id);
        if (cuenta == null) {
            throw new StreamingException("Cuenta no encontrada con ID: " + id);
        }

        Validaciones.validarEmail(email);
        Validaciones.validarTextoNoVacio(password, "Contraseña");
        Validaciones.validarTextoNoVacio(plan, "Plan");
        Validaciones.validarNumeroPositivo(maxEspacios, "Número máximo de espacios");

        if (maxEspacios < cuenta.getEspaciosOcupados()) {
            throw new StreamingException("El máximo de espacios no puede ser menor a los espacios ocupados ("
                    + cuenta.getEspaciosOcupados() + ").");
        }

        for (CuentaStreaming c : cuentas) {
            if (!c.getId().equals(id) && c.getEmail().equalsIgnoreCase(email.trim()) &&
                c.getPlataforma() != null && c.getPlataforma().equals(cuenta.getPlataforma())) {
                throw new StreamingException("Ya existe otra cuenta con el email: " + email + " en la plataforma " + cuenta.getPlataforma().getNombre());
            }
        }

        cuenta.setEmail(email.trim());
        cuenta.setPassword(password);
        cuenta.setPlan(plan.trim());
        cuenta.setMaxEspacios(maxEspacios);
        cuenta.setNotas(notas);
    }

    public void eliminarCuenta(String id) throws StreamingException {
        CuentaStreaming cuenta = buscarCuentaPorId(id);
        if (cuenta == null) {
            throw new StreamingException("Cuenta no encontrada con ID: " + id);
        }

        if (cuenta.tieneEspaciosOcupados()) {
            throw new StreamingException("No se puede eliminar la cuenta porque tiene espacios ocupados.");
        }

        if (cuenta.getPlataforma() != null) {
            cuenta.getPlataforma().removerCuenta(cuenta);
        }

        cuentas.remove(cuenta);
    }

    public CuentaStreaming buscarCuentaPorId(String id) {
        return cuentas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public CuentaStreaming buscarCuentaPorEmail(String email) {
        return cuentas.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public List<CuentaStreaming> listarCuentasPorPlataforma(PlataformaStreaming plataforma) {
        return cuentas.stream()
                .filter(c -> c.getPlataforma() != null && c.getPlataforma().equals(plataforma))
                .collect(Collectors.toList());
    }

    public List<CuentaStreaming> listarCuentasConEspaciosDisponibles() {
        return cuentas.stream()
                .filter(CuentaStreaming::tieneEspaciosDisponibles)
                .collect(Collectors.toList());
    }

    public List<CuentaStreaming> listarCuentas() {
        return new ArrayList<>(cuentas);
    }

    public void cargarCuentas(List<CuentaStreaming> listaCuentas) {
        this.cuentas = new ArrayList<>(listaCuentas);
    }

    public int getTotalCuentas() {
        return cuentas.size();
    }

    public int getTotalEspaciosDisponibles() {
        return cuentas.stream()
                .mapToInt(CuentaStreaming::getEspaciosDisponibles)
                .sum();
    }

    public int getTotalEspaciosMaximos() {
        return cuentas.stream()
                .mapToInt(CuentaStreaming::getMaxEspacios)
                .sum();
    }
}
