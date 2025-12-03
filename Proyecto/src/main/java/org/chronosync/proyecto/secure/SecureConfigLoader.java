package org.chronosync.proyecto.secure;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class SecureConfigLoader {

    public static Properties load() throws Exception {
        File file = getSecureConfigFile();

        if (!file.exists()) {
            throw new IllegalStateException("No se encontró config.secure en: " + file.getAbsolutePath());
        }

        // Cargamos los valores cifrados
        Properties enc = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            enc.load(fis);
        }

        // Desciframos cada valor
        Properties dec = new Properties();
        dec.setProperty("db.url", CryptoUtils.decrypt(enc.getProperty("db.url")));
        dec.setProperty("db.user", CryptoUtils.decrypt(enc.getProperty("db.user")));
        dec.setProperty("db.pass", CryptoUtils.decrypt(enc.getProperty("db.pass")));

        return dec;
    }

    public static File getSecureConfigFile() {
        String os = System.getProperty("os.name").toLowerCase();
        String base;

        if (os.contains("win")) {
            base = System.getenv("LOCALAPPDATA") + "\\ChronoSync\\";
        } else if (os.contains("mac")) {
            base = System.getProperty("user.home") + "/Library/Application Support/ChronoSync/";
        } else {
            base = System.getProperty("user.home") + "/.chronosync/";
        }

        return new File(base + "config.secure");
    }
}
