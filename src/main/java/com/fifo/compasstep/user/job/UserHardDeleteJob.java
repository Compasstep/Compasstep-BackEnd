package com.fifo.compasstep.user.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserHardDeleteJob {

    private final JdbcTemplate jdbc;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매일 00:00 KST
    @Transactional
    public void run() {
        int deleted = jdbc.update("""
            DELETE FROM users
             WHERE is_deleted = true
               AND updated_at <= NOW() - INTERVAL '3 days'
        """);
        log.info("Hard-deleted users: {}", deleted);
    }
}