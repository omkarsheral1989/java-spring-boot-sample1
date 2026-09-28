package com.example.javasbtemp1.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public PatchUserDto() {
    }

    public PatchUserDto(String name, String email, AddressDto address) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.addressProvided = address != null;
    }

    public void setAddress(AddressDto address) {
        this.addressProvided = true;
        this.address = address;
    }
}
