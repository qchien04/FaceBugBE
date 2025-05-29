package com.repository;

import com.DTO.FriendDTO;
import com.entity.auth.User;
import com.entity.auth.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepo extends JpaRepository<UserProfile,Integer> {
    Optional<UserProfile> findByName(String name);
    Optional<UserProfile> findById(int id);
    Optional<UserProfile> findByUserId(Integer userId);

    @Query("select u from UserProfile u where u.name like %:query%")
    List<UserProfile> searchUserProfile(@Param("query") String query);

    @Query("select new com.DTO.FriendDTO(up.id, up.name, up.avt) from UserProfile up where up.name like %:key% and up.accountType = AccountType.PAGE")
    Page<FriendDTO> searchPageProfiles(@Param("key") String key, Pageable pageable);

    @Query("select new com.DTO.FriendDTO(up.id, up.name, up.avt) from UserProfile up where up.name like %:key% and up.accountType = AccountType.NORMAL")
    Page<FriendDTO> searchNormalUserProfiles(@Param("key") String key,Pageable pageable);

    @Query("select new com.DTO.FriendDTO(up.id, up.name, up.avt) from UserProfile up where up.id=:id")
    FriendDTO searchUserProfileDTO(@Param("id") Integer id);
}
