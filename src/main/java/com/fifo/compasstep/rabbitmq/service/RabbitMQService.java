package com.fifo.compasstep.rabbitmq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitMQService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환을 위해 ObjectMapper 주입

    public void sendRetrainingTask(String jobId) throws Exception {
        String exchangeName = "ai_tasks_exchange";
        String routingKey = "retrain.start";
        String taskName = "tasks.start_retraining";

        // 1. Celery 메시지 본문 생성 (이전과 동일)
        Map<String, String> kwargs = new HashMap<>();
        kwargs.put("jobId", jobId);
        kwargs.put("dataSource", "all_user_comments");

        Object[] body = {Collections.emptyList(), kwargs, Collections.emptyMap()};
        String bodyJson = objectMapper.writeValueAsString(body);

        // 2. MessagePropertiesBuilder를 사용하여 속성 설정 (수정된 부분)
        MessageProperties properties = MessagePropertiesBuilder.newInstance()
                .setContentType("application/json") // "application/json"
                .setContentEncoding("UTF-8")
                .setHeader("task", taskName)
                .setHeader("id", jobId)
                .build();

        // 3. 최종 메시지 생성 및 전송 (이전과 동일)
        Message message = new Message(bodyJson.getBytes(), properties);
        rabbitTemplate.send(exchangeName, routingKey, message);

        System.out.println("Celery 형식으로 작업 발행 완료: " + jobId);
    }
}
