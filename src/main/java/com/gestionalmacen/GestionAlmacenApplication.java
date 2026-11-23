
package com.gestionalmacen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestionAlmacenApplication {

    public static void main(String[] args) {

        SpringApplication.run(GestionAlmacenApplication.class, args);

        System.out.println("========================================");
        System.out.println("   SISTEMA DE GESTION DE ALMACEN");
        System.out.println("========================================");
        System.out.println("   APLICACION INICIADA CORRECTAMENTE");
        System.out.println("   SERVIDOR SPRING BOOT EN EJECUCION");
        System.out.println("========================================");
    }

}
