package edu.kalum.auth.core;

import edu.kalum.auth.core.verticles.MainVerticle;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class AuthKalumApplication implements CommandLineRunner {

	@Autowired
	private MainVerticle mainVerticle;

	public static void main(String[] args) {
		SpringApplication.run(AuthKalumApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Vertx.vertx().deployVerticle(mainVerticle)
				.onSuccess(id -> System.out.print("Deployment verticle id " + id))
				.onFailure(error -> System.out.print("Failed deployment verticle ".concat(error.getMessage())));


	}
}
