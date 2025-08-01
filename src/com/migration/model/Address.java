package com.migration.model;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class Address {
    @NotBlank(message = "Street name is required")
    private String streetName;

    private String floor;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "County is required")
    private String county;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Zipcode is required")
    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$", message = "Invalid zipcode format")
    private String zipcode;
}
