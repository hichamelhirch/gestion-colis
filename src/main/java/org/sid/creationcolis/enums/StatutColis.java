package org.sid.creationcolis.enums;

public enum StatutColis {
    BROUILLON,CONFIRMER,ANNULER;

    public static StatutColis fromString(String status) {
        try {
            return StatutColis.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }
}
