package org.chronosync.proyecto.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class CargarImagenUtil {

    /**
     * Carga una imagen desde los recursos y la pone en el ImageView que le digamos
     *
     * @param imagen ImageView donde queremos que se ponga la imagen
     * @param ruta dirección de la imagen
     */
    public static void establecerImagen(ImageView imagen, String ruta) {
        try {
            // Abrimos la imagen
            InputStream stream = CargarImagenUtil.class.getResourceAsStream(ruta);

            if (stream != null) {
                // Si el archivo existe, creamos y ponemos la imagen
                imagen.setImage(new Image(stream));
            } else {
                // Si no existe, avisamos por consola
                System.err.println("Imagen no encontrada: " + ruta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}