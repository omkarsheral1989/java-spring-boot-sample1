package com.example.javasbtemp1.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserPatchRequest {

    private String name;

    @Email
    private String email;

    private String address;
}
