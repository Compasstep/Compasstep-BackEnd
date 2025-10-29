package com.fifo.compasstep.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    /**
     * 스케줄러 스레드풀 설정
     */
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);                   // 동시에 돌 스케줄 작업 수
        scheduler.setThreadNamePrefix("scheduler-");
        scheduler.setRemoveOnCancelPolicy(true);   // 취소 시 큐에서 제거
        scheduler.setAwaitTerminationSeconds(30);  // 종료 대기 시간
        return scheduler;
    }
}