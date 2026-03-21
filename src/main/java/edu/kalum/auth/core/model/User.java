package edu.kalum.auth.core.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends Person {
    private String username;
    private String password;
    private String applicationNumber;
    private List<Role> role;
}
