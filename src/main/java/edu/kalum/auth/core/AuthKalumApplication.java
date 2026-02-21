package edu.kalum.auth.core;

import edu.kalum.auth.core.model.Person;
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

		Role roleUser = new Role(1, "ROLE_USER");
		Role roleApplicant = new Role(2, "ROLE_APPLICANT");
		Role roleStudent = new Role(3, "ROLE_STUDENT");

		User edwin = new User("1", "Tumax","Edwin","edwintumax@gmail.com","Guatemala, Guatemala","33124569","edwintumax","123", roleUser);
		User jose = new User("2","Garcia","Luis","luisgarcia@gmail.com","Guatemala, Guatemala","24711529","luisgarcia","789456",roleApplicant);
		User maria = new User("3","Mancilla","Maria","mariamancilla@gmail.com","Guatemala, Guatemala","23224578","mmancilla","x45789",roleStudent)

		System.out.println("Detalle de usuario: ".concat(JsonObject.mapFrom(edwin).encodePrettily()));
		System.out.println("Detalle de aplicante: ".concat(JsonObject.mapFrom(jose).encodePrettily()));
		System.out.println("Detalle de estudiante: ".concat(JsonObject.mapFrom(maria).encodePrettily()));


	}
}
