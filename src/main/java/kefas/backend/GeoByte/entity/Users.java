package kefas.backend.GeoByte.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import kefas.backend.GeoByte.enums.Role;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotEmpty(message = "First Name cannot be empty")
  @Column(name = "first_name")
  private String firstname;

  @NotEmpty(message = "Last Name cannot be empty")
  @Column(name = "last_name")
  private String lastname;

  @Email
  private String email;

  @NotEmpty(message = "Password cannot be empty")
  @Column(name = "password")
  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;
}
