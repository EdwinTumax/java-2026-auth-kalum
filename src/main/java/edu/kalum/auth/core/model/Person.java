package edu.kalum.auth.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {
    private String userId;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
}
