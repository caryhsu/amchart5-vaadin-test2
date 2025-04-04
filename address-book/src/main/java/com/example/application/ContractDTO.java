package com.example.application;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ContractDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
