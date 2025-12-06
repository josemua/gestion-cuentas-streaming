package com.josemuadev.trabajofinal.util;

import java.util.UUID;

public class GeneradorId {

    public static String generarId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generarId(String prefijo) {
        return prefijo + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}