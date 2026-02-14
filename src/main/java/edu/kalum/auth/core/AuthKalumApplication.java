package edu.kalum.auth.core;

import edu.kalum.auth.core.model.Role;
import edu.kalum.auth.core.model.Student;
import edu.kalum.auth.core.model.User;
import io.vertx.core.json.JsonObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthKalumApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AuthKalumApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Role role = new Role(1, "ROLE_ADMIN");
		User edwin = new User("edwintumax","123", role);
		Student jose = new Student();
		System.out.println("Detalle de usuario: ".concat(JsonObject.mapFrom(edwin).encodePrettily()));
		System.out.println("Detalle de estudiante: ".concat(JsonObject.mapFrom(jose).encodePrettily()));
	}
}
