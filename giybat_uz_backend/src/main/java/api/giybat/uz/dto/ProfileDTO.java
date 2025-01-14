package api.giybat.uz.dto;

import api.giybat.uz.enums.ProfileRole;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String username;
    private List<ProfileRole> roleList;
    private String jwt;
}
