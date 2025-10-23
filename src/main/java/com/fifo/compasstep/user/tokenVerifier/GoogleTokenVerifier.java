package com.fifo.compasstep.user.tokenVerifier;

import com.fifo.compasstep.user.dto.GoogleUserInfo;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class GoogleTokenVerifier {

    @Value("${google.client-id}")
    private String googleClientId;

    public GoogleUserInfo verify(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    // 이 토큰이 우리 서비스(앱)를 위해 발급된 것이 맞는지 Client ID로 확인
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // payload에서 필요한 정보만 추출
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // 추출한 정보로 우리의 DTO인 GoogleUserInfo 객체를 생성하여 반환
                return new GoogleUserInfo(email, name);
            }
        } catch (Exception e) {
            // 토큰이 유효하지 않거나 검증 과정에서 오류 발생
            throw new IllegalArgumentException("유효하지 않은 Google ID 토큰입니다.", e);
        }

        // 토큰 검증 실패
        throw new IllegalArgumentException("유효하지 않은 Google ID 토큰입니다.");
    }
}