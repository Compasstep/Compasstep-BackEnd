package com.fifo.compasstep.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void encodePassword() {
        String password = "password123"; // 암호화하고 싶은 원본 비밀번호
        String encodedPassword = passwordEncoder.encode(password);

        System.out.println("========================================");
        System.out.println("Encoded Password: " + encodedPassword);
        System.out.println("========================================");
    }
}