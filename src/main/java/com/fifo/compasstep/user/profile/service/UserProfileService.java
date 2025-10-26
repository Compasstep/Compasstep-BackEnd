// src/main/java/com/fifo/compasstep/user/profile/service/UserProfileService.java
package com.fifo.compasstep.user.profile.service;

import com.fifo.compasstep.user.domain.User;
import com.fifo.compasstep.user.repository.UserRepository;
import com.fifo.compasstep.user.profile.dto.response.UserProfileInfoResponseDTO;
import com.fifo.compasstep.apipayload.exceptions.handler.UserHandler;
import com.fifo.compasstep.user.profile.exceptions.UserProfileErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final UserRepository userRepository;

    /** DB에 저장된 값 그대로 반환 (프리사인드 URL은 나중에 별도 API로) */
    @Transactional(Transactional.TxType.SUPPORTS)
    public UserProfileInfoResponseDTO getProfileInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(UserProfileErrorStatus.USER_NOT_FOUND));

        return UserProfileInfoResponseDTO.builder()
                .profileImageUrl(user.getS3FileImage()) // DB 원본 값 그대로
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    /** 프로필 이미지 경로 업데이트 (DB값만 저장) */
    public void updateProfileImage(Long userId, String fileKey) {
        // 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(UserProfileErrorStatus.USER_NOT_FOUND));

        int updated = userRepository.updateProfileImage(userId, fileKey);
        if (updated == 0) {
            throw new UserHandler(UserProfileErrorStatus.USER_NOT_FOUND);
        }
    }

    /** 닉네임 업데이트 */
    public void updateNickname(Long userId, String nickname) {
        // 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(UserProfileErrorStatus.USER_NOT_FOUND));

        int updated = userRepository.updateNickname(userId, nickname);
        if (updated == 0) {
            throw new UserHandler(UserProfileErrorStatus.USER_NOT_FOUND);
        }
    }
}
