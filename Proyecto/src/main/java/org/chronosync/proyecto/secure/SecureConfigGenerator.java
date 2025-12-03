package org.chronosync.proyecto.secure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Properties;

public class SecureConfigGenerator {

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("=== Generador de config.secure ===");
            System.out.print("DB URL: ");
            String url = br.readLine().trim();

            System.out.print("DB USER: ");
            String user = br.readLine().trim();

            System.out.print("DB PASS: ");
            String pass = br.readLine().trim();

            Properties enc = new Properties();
            enc.setProperty("db.url", CryptoUtils.encrypt(url));
            enc.setProperty("db.user", CryptoUtils.encrypt(user));
            enc.setProperty("db.pass", CryptoUtils.encrypt(pass));

            File dir = SecureConfigLoader.getSecureConfigFile().getParentFile();
            dir.mkdirs();

            File outFile = SecureConfigLoader.getSecureConfigFile();
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                enc.store(fos, "Archivo seguro - No modificar");
            }

            System.out.println("Archivo creado en: " + outFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
