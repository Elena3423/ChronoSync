package org.chronosync.proyecto.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class CargarImagenUtil {

    public static void establecerImagen(ImageView imagen, String ruta) {
        try {
            InputStream stream = CargarImagenUtil.class.getResourceAsStream(ruta);
            if (stream != null) {
                imagen.setImage(new Image(stream));
            } else {
                // Un error, pero al menos solo aparece en un lugar
                System.err.println("Imagen no encontrada: " + ruta);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
