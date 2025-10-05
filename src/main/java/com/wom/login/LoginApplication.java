package com.wom.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class LoginApplication {

	public static void main(String[] args) {
		// Cargar variables del .env antes de que Spring arranque
		Dotenv dotenv = Dotenv.load();

		// Opcionalmente establecerlas como propiedades del sistema
		System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
		System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
		System.setProperty("JWT_PRIVATE_KEY", dotenv.get("JWT_PRIVATE_KEY"));
		System.setProperty("JWT_PUBLIC_KEY", dotenv.get("JWT_PUBLIC_KEY"));
		System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
		SpringApplication.run(LoginApplication.class, args);
	}

}
