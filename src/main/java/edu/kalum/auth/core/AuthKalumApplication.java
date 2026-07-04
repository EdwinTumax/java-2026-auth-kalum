package edu.kalum.auth.core;

import edu.kalum.auth.core.verticles.MainVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class AuthKalumApplication implements CommandLineRunner {

	@Autowired
	private MainVerticle mainVerticle;

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(AuthKalumApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String envConfig = environment.getProperty("SPRING_PROFILES_ACTIVE") != null ? environment.getProperty("SPRING_PROFILES_ACTIVE") : "dev";
		ConfigStoreOptions configStoreOptionsEnv = new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path",envConfig.concat(".json")));
		ConfigStoreOptions configStoreOptionsSys = new ConfigStoreOptions().setType("sys");
		ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions().addStore(configStoreOptionsEnv).addStore(configStoreOptionsSys);
		ConfigRetriever configRetriever = ConfigRetriever.create(Vertx.vertx(),configRetrieverOptions);

		configRetriever.getConfig().onSuccess(config -> {
			Vertx.vertx().deployVerticle(mainVerticle, new DeploymentOptions().setConfig(config))
					.onSuccess(id -> System.out.print("Deployment verticle id " + id))
					.onFailure(error -> System.out.print("Failed deployment verticle ".concat(error.getMessage())));
		}).onFailure(error -> {
			System.out.println("Error in Deployment verticle");
		});

	}
}
