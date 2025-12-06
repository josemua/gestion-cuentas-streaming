package com.josemuadev.trabajofinal.util;

import java.util.regex.Pattern;

public class Validaciones {

    private static final Pattern PATRON_EMAIL = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PATRON_TELEFONO = Pattern.compile(
            "^[0-9]{7,15}$"
    );

    public static void validarTextoNoVacio(String texto, String nombreCampo) throws StreamingException {
        if (texto == null || texto.trim().isEmpty()) {
            throw new StreamingException("El campo '" + nombreCampo + "' no puede estar vacío.");
        }
    }

    public static void validarEmail(String email) throws StreamingException {
        validarTextoNoVacio(email, "Email");
        if (!PATRON_EMAIL.matcher(email).matches()) {
            throw new StreamingException("El email '" + email + "' no tiene un formato válido.");
        }
    }

    public static void validarTelefono(String telefono) throws StreamingException {
        validarTextoNoVacio(telefono, "Teléfono");
        String telefonoLimpio = telefono.replaceAll("[\\s\\-()]", "");
        if (!PATRON_TELEFONO.matcher(telefonoLimpio).matches()) {
            throw new StreamingException("El teléfono debe contener entre 7 y 15 dígitos.");
        }
    }

    public static void validarPrecioPositivo(double precio, String nombreCampo) throws StreamingException {
        if (precio <= 0) {
            throw new StreamingException("El " + nombreCampo + " debe ser mayor a cero.");
        }
    }

    public static void validarNumeroPositivo(int numero, String nombreCampo) throws StreamingException {
        if (numero <= 0) {
            throw new StreamingException("El " + nombreCampo + " debe ser mayor a cero.");
        }
    }

    public static void validarFormatoMes(String mes) throws StreamingException {
        validarTextoNoVacio(mes, "Mes");
        if (!mes.matches("^\\d{4}-\\d{2}$")) {
            throw new StreamingException("El mes debe tener formato YYYY-MM (ej: 2025-01).");
        }
    }
}
