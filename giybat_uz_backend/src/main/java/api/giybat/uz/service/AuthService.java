package api.giybat.uz.service;

import api.giybat.uz.dto.AuthDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRole;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileRoleService profileRoleService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailSendingService emailSendingService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRoleRepository profileRoleRepository;


    public ProfileEntity registration(RegistrationDTO dto) {
        // 1. Validation and username existence check
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus() == GeneralStatus.IN_REGISTRATION) {
                profileRoleService.delete(profile.getId());
                profileRepository.delete(profile);
            }
            throw new AppBadException("Username already exists");
        }

        // 2. Create new profile entity
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setUsername(dto.getUsername());
        entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        entity.setStatus(GeneralStatus.IN_REGISTRATION);
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());

        // 3. Save and return
        ProfileEntity save = profileRepository.save(entity);

        //Insert Roles
        profileRoleService.create(entity.getId(), ProfileRole.ROLE_USER);

        // Send SMS to mail
        emailSendingService.sendRegistrationEmail(entity.getUsername(), entity.getId());

        return save;
    }

    public String regVerification(Integer profileId) {
        ProfileEntity profile = profileService.getById(profileId);
        if (profile == null) {
            throw new AppBadException("Profile not found.");
        }

        if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);
            return "Verification successful";
        }

        throw new AppBadException("Verification failed. Current status: " + profile.getStatus());
    }

    public ProfileDTO login(AuthDTO dto) {

        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (!optional.isPresent()) {
            throw new AppBadException("Username or password is incorrect");
        }
        ProfileEntity profile = optional.get();
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), profile.getPassword())) {
            throw new AppBadException("Username or password is incorrect");
        }
        if (profile.getStatus() != GeneralStatus.ACTIVE) {
            throw new AppBadException("Wrong Status");
        }

        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesListByProfileId(profile.getId()));

        response.setJwt(JwtUtil.encode(profile.getId(), profile.getUsername(), response.getRoleList()));

        return response;
    }
}
