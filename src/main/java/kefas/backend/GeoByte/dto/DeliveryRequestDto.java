package kefas.backend.GeoByte.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DeliveryRequestDto {

    @Email
    @NotEmpty(message = "Email cannot be Empty")
    @Column(name = "email")
    private String email;

    @NotEmpty(message = "Item cannot be empty")
    @Column(name = "item")
    private String item;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private double longitude;

    @Min(25)
    @Max(100)
    private double clearingCost;
}
