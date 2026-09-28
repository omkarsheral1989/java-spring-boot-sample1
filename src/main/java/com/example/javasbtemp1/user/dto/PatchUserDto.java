package com.example.javasbtemp1.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class PatchUserDto {

    private String name;

    @Email
    private String email;

    @Valid
    private AddressDto address;
    private boolean addressProvided;

    public void setAddress(AddressDto address) {
        this.addressProvided = true;
        this.address = address;
    }
}
