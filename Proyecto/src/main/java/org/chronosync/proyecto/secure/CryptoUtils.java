package org.chronosync.proyecto.secure;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.SecretKey;
import javax.crypto.spec.*;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {

    // Frase secreta para derivar la clave AES
    // (se recomienda cambiarla y ofuscarla con ProGuard al final)
    private static final String SECRET = "ChronoSyncClaveSuperSegura2025!";

    private static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_SIZE_BITS = 256;
    private static final int ITERATIONS = 65536;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static SecretKey deriveKey(char[] passphrase, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(passphrase, salt, ITERATIONS, KEY_SIZE_BITS);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KDF_ALGORITHM);
        byte[] keyBytes = skf.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String plain) throws Exception {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);

        SecretKey key = deriveKey(SECRET.toCharArray(), salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] iv = new byte[16];
        RANDOM.nextBytes(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] cipherText = cipher.doFinal(plain.getBytes("UTF-8"));

        byte[] result = new byte[salt.length + iv.length + cipherText.length];

        System.arraycopy(salt, 0, result, 0, salt.length);
        System.arraycopy(iv, 0, result, salt.length, iv.length);
        System.arraycopy(cipherText, 0, result, salt.length + iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(result);
    }

    public static String decrypt(String base64) throws Exception {
        byte[] all = Base64.getDecoder().decode(base64);

        byte[] salt = new byte[16];
        byte[] iv = new byte[16];

        System.arraycopy(all, 0, salt, 0, 16);
        System.arraycopy(all, 16, iv, 0, 16);

        byte[] cipherText = new byte[all.length - 32];
        System.arraycopy(all, 32, cipherText, 0, cipherText.length);

        SecretKey key = deriveKey(SECRET.toCharArray(), salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return new String(cipher.doFinal(cipherText), "UTF-8");
    }
}
