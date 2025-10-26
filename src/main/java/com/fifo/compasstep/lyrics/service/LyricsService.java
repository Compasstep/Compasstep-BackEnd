package com.fifo.compasstep.lyrics.service;

import com.fifo.compasstep.admin.exceptions.AdminErrorStatus;
import com.fifo.compasstep.apipayload.exceptions.handler.AdminHandler;
import com.fifo.compasstep.apipayload.exceptions.handler.UserHandler;
import com.fifo.compasstep.common.dto.StoreRequestDTO;
import com.fifo.compasstep.common.dto.StoreResponseDTO;
import com.fifo.compasstep.lyrics.domain.Lyrics;
import com.fifo.compasstep.lyrics.repository.LyricsRepository;
import com.fifo.compasstep.security.userDetails.AdminUserDetails;
import com.fifo.compasstep.security.userDetails.UserUserDetails;
import com.fifo.compasstep.user.exceptions.UserErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LyricsService {
    private final LyricsRepository lyricsRepository;

    @Transactional
    public StoreResponseDTO storeLyrics(StoreRequestDTO request) {
        // 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserUserDetails)) {
            // 인증 정보가 없거나, 예상치 못한 Principal 타입인 경우 예외 처리
            throw new UserHandler(UserErrorStatus.USER_NOT_FOUND); // 적절한 에러 상태 정의 필요
        }
        // 현재 로그인 된 사용자 정보 / 이름 / 파일 키 가져옴
        UserUserDetails currentUserDetails = (UserUserDetails) authentication.getPrincipal();
        String title = request.getTitle();
        String fileKey = request.getFileKey();


        Lyrics lyrics = Lyrics.builder()
                .title(title)
                .s3Lyrics(fileKey)
                .user(currentUserDetails.getUser())
                .build();
        Lyrics saved = lyricsRepository.save(lyrics);

        return StoreResponseDTO.builder()
                .content_id(saved.getId())
                .build();
    }
}
