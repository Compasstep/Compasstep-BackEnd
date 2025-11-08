package com.fifo.compasstep.rabbitmq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks") // 이 컨트롤러의 모든 API는 /api/tasks로 시작
public class TestController {

    // 이전에 만든 RabbitMQService를 주입받습니다. (매니저가 전문가를 호출)
    private final RabbitMQService rabbitMQService;

    @Autowired
    public TestController(RabbitMQService rabbitMQService) {
        this.rabbitMQService = rabbitMQService;
    }

    // 재학습을 시작시키는 API 엔드포인트
    @PostMapping("/retrain/start")
    public ResponseEntity<String> startRetrainingTask() throws Exception {
        // 1. 추적을 위한 고유한 Job ID 생성
        String jobId = UUID.randomUUID().toString();

        // 2. 주입받은 서비스를 호출하여 실제 메시지 발행
        rabbitMQService.sendRetrainingTask(jobId);

        // 3. 클라이언트에게 작업이 시작되었음을 알리는 응답 즉시 전송
        return ResponseEntity.ok("AI retraining task has been queued with Job ID: " + jobId);
    }
}
