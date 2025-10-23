// src/main/java/com/fifo/compasstep/user/repository/UserRepository.java
package com.fifo.compasstep.user.repository;

import com.fifo.compasstep.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 조회용
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);

    // 수정용 (벌크 업데이트)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update User u set u.s3FileImage = :fileKey where u.id = :userId")
    int updateProfileImage(@Param("userId") Long userId, @Param("fileKey") String fileKey);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update User u set u.nickname = :nickname where u.id = :userId")
    int updateNickname(@Param("userId") Long userId, @Param("nickname") String nickname);
}
