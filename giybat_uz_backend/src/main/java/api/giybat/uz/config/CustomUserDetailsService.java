package api.giybat.uz.config;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.entity.ProfileRolesEntity;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(username);
        if (!optional.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
        ProfileEntity profile = optional.get();
        Integer profileId = profile.getId();
        List<ProfileRolesEntity> profileRolesEntities = profileRoleRepository.findByProfileId(profileId);
        List<ProfileRole> profileRoles = new ArrayList<>();
        for (ProfileRolesEntity profileRolesEntity : profileRolesEntities) {
            profileRoles.add(profileRolesEntity.getRoles());
        }
        return new CustomUserDetails(profile, profileRoles);
    }
}
