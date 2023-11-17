package kefas.backend.GeoByte.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRoute {

    private List<DeliveryLocation> locations;
    private double totalCost;
}
