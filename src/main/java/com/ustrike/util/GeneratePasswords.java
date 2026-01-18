package com.ustrike.util;

public class GeneratePasswords {
    public static void main(String[] args) {
        // Generiamo gli hash per i due dipendenti
        String hashMario = PasswordHasher.hash("Mariostaff5");
        String hashLuca = PasswordHasher.hash("LucaStaff4");

        System.out.println("--- COPIA QUESTI VALORI NEL TUO SQL ---");
        System.out.println("Hash per Mario: " + hashMario);
        System.out.println("Hash per Luca: " + hashLuca);
    }
}