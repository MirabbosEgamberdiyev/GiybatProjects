package api.giybat.uz.entity;

import api.giybat.uz.enums.ProfileRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "profile_role")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRolesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the profile role", example = "1")
    private Integer id;

    @Column(name = "profile_id", nullable = false)
    @Schema(description = "Identifier of the associated profile", example = "101")
    private Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    @Schema(description = "Associated profile entity")
    private ProfileEntity profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Schema(description = "Role assigned to the profile", example = "ROLE_ADMIN", allowableValues = {"ROLE_ADMIN", "ROLE_USER"})
    private ProfileRole roles;

    @Column(name = "created_date", nullable = false, updatable = false)
    @Schema(description = "Timestamp when the role was created", example = "2025-01-01T10:00:00")
    private LocalDateTime createdDate;
}
