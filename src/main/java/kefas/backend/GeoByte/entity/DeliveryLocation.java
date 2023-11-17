package kefas.backend.GeoByte.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_location")
public class DeliveryLocation{

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
