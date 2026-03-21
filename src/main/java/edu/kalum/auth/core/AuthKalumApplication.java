package edu.kalum.auth.core;

import edu.kalum.auth.core.model.Person;
import edu.kalum.auth.core.model.Role;
import edu.kalum.auth.core.model.User;
import io.vertx.core.json.JsonObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class AuthKalumApplication implements CommandLineRunner {

	private static PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(AuthKalumApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
