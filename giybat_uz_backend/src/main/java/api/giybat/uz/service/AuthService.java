package api.giybat.uz.service;

import api.giybat.uz.dto.AuthDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
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

    @Autowired
    private ResourceBundleService resourceBundleService;

    public ProfileEntity registration(RegistrationDTO dto, AppLanguage language) {
        // 1. Validation and username existence check
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus() == GeneralStatus.IN_REGISTRATION) {
                profileRoleService.delete(profile.getId());
                profileRepository.delete(profile);
            }
            String errorMessage = resourceBundleService.getMessage("registration.username_exists", language);
            throw new AppBadException(errorMessage);
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
        ProfileEntity savedProfile = profileRepository.save(entity);

        // Insert Roles
        profileRoleService.create(entity.getId(), ProfileRole.ROLE_USER);

        // Send registration email
        emailSendingService.sendRegistrationEmail(entity.getUsername(), entity.getId());

        return savedProfile;
    }

    public String regVerification(Integer profileId, AppLanguage language) {
        ProfileEntity profile = profileService.getById(profileId);
        if (profile == null) {
            String errorMessage = resourceBundleService.getMessage("verification.profile_not_found", language);
            throw new AppBadException(errorMessage);
        }

        if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);
            return resourceBundleService.getMessage("verification.success", language);
        }

        String errorMessage = resourceBundleService.getMessage("verification.status_invalid", language);
        throw new AppBadException(errorMessage);
    }

    public ProfileDTO login(AuthDTO dto, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (!optional.isPresent()) {
            String errorMessage = resourceBundleService.getMessage("login.username_password_incorrect", language);
            throw new AppBadException(errorMessage);
        }

        ProfileEntity profile = optional.get();
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), profile.getPassword())) {
            String errorMessage = resourceBundleService.getMessage("login.username_password_incorrect", language);
            throw new AppBadException(errorMessage);
        }

        if (profile.getStatus() != GeneralStatus.ACTIVE) {
            String errorMessage = resourceBundleService.getMessage("login.status_invalid", language);
            throw new AppBadException(errorMessage);
        }

        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesListByProfileId(profile.getId()));

        response.setJwt(JwtUtil.encode(profile.getId(), profile.getUsername(), response.getRoleList()));

        return response;
    }
}
