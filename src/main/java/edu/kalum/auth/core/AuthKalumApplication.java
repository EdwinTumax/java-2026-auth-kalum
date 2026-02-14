package edu.kalum.auth.core;

import edu.kalum.auth.core.model.Role;
import edu.kalum.auth.core.model.User;
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

		User edwin = new User();
		edwin.setUsername("edwintumax");
		edwin.setPassword("123");

		Role role = new Role();
		role.setId(1);
		role.setName("ROLE_ADMIN");

		edwin.setRole(role);

		System.out.println("Detalle de usuario: ".concat(edwin.getUsername()).concat(" ").concat(edwin.getPassword()).concat(" ").concat(edwin.getRole().getName()));
	}
}
