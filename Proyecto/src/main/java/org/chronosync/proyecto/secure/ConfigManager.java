package org.chronosync.proyecto.secure;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigManager {
    private static final boolean MODO_DESARROLLO = true;
    // Cuando generéis el ejecutable final → poner en false

    public static Properties getConfig() throws Exception {
        Properties p = new Properties();

        if (MODO_DESARROLLO) {
            try (FileInputStream fis = new FileInputStream("src/main/resources/config.dev")) {
                p.load(fis);
            }
        } else {
            p = SecureConfigLoader.load();
        }

        return p;
    }
}
