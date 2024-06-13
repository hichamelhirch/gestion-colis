/*package org.sid.creationcolis.mappers;

import com.google.zxing.BarcodeFormat;

import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

@Component
public class BarcodeGenerator {

    public String generateBarcode(String data) {
        try {
            // Créez un objet EAN13Writer
            EAN13Writer writer = new EAN13Writer();

            // Ajoutez des zéros à gauche si nécessaire pour obtenir un code à 13 chiffres
            data = String.format("%013d", Long.parseLong(data));

            // Créez un BitMatrix en utilisant les données pour générer le code-barres
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.EAN_13, 300, 150);

            // Convertir le BitMatrix en une image BufferedImage
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convertir l'image BufferedImage en tableau d'octets (byte[])
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] barcodeImage = baos.toByteArray();

            // Vous pouvez enregistrer l'image, retourner l'image en base64 ou un autre format
            return data; // Retournez le code-barres comme String
        } catch ( IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}



 */
