package org.sid.creationcolis.config;

import org.sid.creationcolis.repository.ColisRepository;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class BarcodeGenerator {

    private final ColisRepository colisRepository;

    public BarcodeGenerator(ColisRepository colisRepository) {
        this.colisRepository = colisRepository;
    }

    public String generateUniqueBarcode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder barcode = new StringBuilder();
        Random random = new Random();

        String generatedBarcode;
        do {
            barcode.setLength(0);
            for (int i = 0; i < 12; i++) {
                barcode.append(characters.charAt(random.nextInt(characters.length())));
            }
            generatedBarcode = barcode.toString();
        } while (barcodeExists(generatedBarcode));

        return generatedBarcode;
    }

    private boolean barcodeExists(String barcode) {
        return colisRepository.existsByCodeBarre(barcode);
    }
}
