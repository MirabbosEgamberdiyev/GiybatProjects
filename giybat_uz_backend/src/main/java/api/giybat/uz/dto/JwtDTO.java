package api.giybat.uz.dto;

import api.giybat.uz.enums.ProfileRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtDTO {
    private Integer id;
    private String username;
    private List<ProfileRole> roleList;
}
