package com.migration.model;

import lombok.Data;
import javax.validation.constraints.*;
import javax.validation.Valid;

@Data
public class PaymentRequest {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[0-9]{8,17}$", message = "Invalid account number format")
    private String accountNumber;

    @NotBlank(message = "Routing number is required")
    @Pattern(regexp = "^[0-9]{9}$", message = "Invalid routing number format")
    private String routingNumber;

    @NotBlank(message = "Beneficiary name is required")
    @Size(min = 2, max = 100, message = "Beneficiary name must be between 2 and 100 characters")
    private String beneficiaryName;

    @NotNull(message = "Address is required")
    @Valid
    private Address address;
}

