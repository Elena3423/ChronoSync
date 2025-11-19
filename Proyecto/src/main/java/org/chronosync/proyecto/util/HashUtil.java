package org.chronosync.proyecto.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Clase de utilidad para generar hashes
 * Se utiliza para hashear contraseñas antes de almacenarlas,
 * lo cual es una práctica de seguridad estandar
 */
public class HashUtil {

    /**
     * Método que genera un hash SHA-256 para la contraseña
     *
     * @param input texto plano a cifrar
     * @return hash 256 en formato hexadecimal
     */
    public static String sha256(String input) {
        try {
            // Obtenemos una instancia de MessageDigest para el algoritmo SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Calculamos el hash
            // input.getBytes() convierte la cadena en un array de bytes
            // md.digest() calcula el hash SHA-256 de esos bytes
            byte[] hashBytes = md.digest(input.getBytes());

            // Convertimos el array de bytes del hash a una cadena hexadecimal legible
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                // Indicamos el formato hexadecimal (x)
                // Aseguramos que tenga al menos dos dígitos, rellenando con un 0 inicial (02)
                sb.append(String.format("%02x", b));
            }

            // Devolvemos la cadena hexadecimal completa del hash
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // Esta excepción sucede si Java no soporta SHA-256, ya que no se suele usar en entornos modernos
            throw new RuntimeException("Error al generar SHA-256", e);
        }
    }

}
