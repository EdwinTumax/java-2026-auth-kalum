package edu.kalum.auth.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDTO {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String applicationNumber;
}
