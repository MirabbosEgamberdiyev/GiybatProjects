package api.giybat.uz.entity;

import api.giybat.uz.enums.GeneralStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "profile")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier of the profile", example = "1")
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(description = "Full name of the user", example = "John Doe", maxLength = 100)
    private String name;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    @Size(max = 100, message = "Username cannot exceed 100 characters")
    @Schema(description = "Unique username, either an email or phone number", example = "johndoe@example.com", maxLength = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    @Size(max = 255, message = "Password cannot exceed 255 characters")
    @Schema(description = "Encrypted password of the user", example = "encrypted_password_hash", maxLength = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Schema(description = "Status of the profile", example = "ACTIVE", allowableValues = {"ACTIVE", "BLOCK"})
    private GeneralStatus status;

    @Column(name = "visible", nullable = false)
    @Schema(description = "Visibility status of the profile", example = "true")
    private Boolean visible;

    @Column(name = "created_date", nullable = false, updatable = false)
    @Schema(description = "Timestamp when the profile was created", example = "2025-01-01T10:00:00")
    private LocalDateTime createdDate;

}
