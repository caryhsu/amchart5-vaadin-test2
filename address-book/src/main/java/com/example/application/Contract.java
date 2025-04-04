package com.example.application;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
public class Contract {

    @Id @GeneratedValue
    private Long id;

    @Setter
    private String firstName;

    @Setter
    private String lastName;

    @Setter
    private String email;

    @Setter
    private String phone;

    public Contract(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
}
