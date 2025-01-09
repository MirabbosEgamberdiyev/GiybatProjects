package api.giybat.uz.service;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.exps.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public ProfileEntity getById(Integer id) {
        return profileRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() ->
                        new AppBadException("Profile Not found"));
    }


}
