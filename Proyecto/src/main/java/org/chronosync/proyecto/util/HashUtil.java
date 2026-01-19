package org.chronosync.proyecto.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    /**
     * Método que convierte cualquier texto en una huella digital única usando SHA-256
     *
     * @param input texto a cifrar
     * @return cadena de 64 caracteres hexadecimales (hash)
     */
    public static String sha256(String input) {
        try {
            // Obtenemos una instancia de MessageDigest para el algoritmo SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Calculamos el hash
            // input.getBytes() convierte la cadena en un array de bytes
            // md.digest() calcula el hash SHA-256 de esos bytes
            byte[] hashBytes = md.digest(input.getBytes());

            // Convertimos el array de bytes del hash a una cadena hexadecimal que se pueda leer
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                // Indicamos el formato hexadecimal (x)
                // Aseguramos que tenga al menos dos dígitos, rellenando con un 0 inicial (02)
                sb.append(String.format("%02x", b));
            }

            // Devolvemos la cadena hexadecimal completa del hash
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar SHA-256: ", e);
        }
    }

}
