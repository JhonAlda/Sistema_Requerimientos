package com.gestionalmacen.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarPassword {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = dotenv.get("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            System.out.println("No se encontró DB_PASSWORD en el .env");
            return;
        }

        String hash = encoder.encode(password);

        System.out.println("Hash BCrypt generado:");
        System.out.println(hash);
    }
}