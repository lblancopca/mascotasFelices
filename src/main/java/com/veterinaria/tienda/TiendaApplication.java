package com.veterinaria.tienda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TiendaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaApplication.class, args);
		System.out.println("==============================================");
        System.out.println("  Clínica Veterinaria Mascotas Felices");
        System.out.println("  Aplicación iniciada correctamente ");
        System.out.println("  URL: http://localhost:8080");
        System.out.println("==============================================");
	}

}
