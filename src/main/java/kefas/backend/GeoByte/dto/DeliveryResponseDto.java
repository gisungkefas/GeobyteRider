package kefas.backend.GeoByte.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class DeliveryResponseDto {

    private Long id;
    private String email;

    private String item;

    private double latitude;

    private double longitude;

    private double clearingCost;
}
