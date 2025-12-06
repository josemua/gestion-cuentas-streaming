package com.josemuadev.trabajofinal.util;

public class StreamingException extends Exception {

    public StreamingException(String mensaje) {
        super(mensaje);
    }

    public StreamingException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
