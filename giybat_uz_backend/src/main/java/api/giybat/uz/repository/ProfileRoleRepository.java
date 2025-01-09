package api.giybat.uz.repository;

import api.giybat.uz.entity.ProfileRolesEntity;
import api.giybat.uz.enums.ProfileRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRoleRepository extends CrudRepository<ProfileRolesEntity, Integer> {

    @Transactional
    @Modifying
    void deleteByProfileId(Integer profileId);

    List<ProfileRolesEntity> findByProfileId(Integer profileId);

    @Query("select p.roles from  ProfileRolesEntity p where p.profileId = ?1")
    List<ProfileRole> getAllRolesListByProfileId(Integer profileId);

}
