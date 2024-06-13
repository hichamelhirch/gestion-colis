package org.sid.creationcolis.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LabelService {

    public Path generateLabel(String barcode, String chargerName, String receiverName) throws Exception {
        // Définir le chemin de sauvegarde
        Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), "label.pdf"); // Chemin temporaire

        // Générer le code-barres
        BitMatrix bitMatrix = new MultiFormatWriter().encode(barcode, BarcodeFormat.CODE_128, 200, 100);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        // Créer le document PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, pngData, "barcode");

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(20, 750);
                contentStream.showText("Chargeur: " + chargerName);
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Destinataire: " + receiverName);
                contentStream.endText();

                contentStream.drawImage(pdImage, 20, 600);
            }

            document.save(filePath.toString());
        }

        return filePath;
    }
}
