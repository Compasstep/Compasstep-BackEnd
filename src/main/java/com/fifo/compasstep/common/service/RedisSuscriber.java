package com.fifo.compasstep.common.service;

public class RedisSuscriber {
    public void handleMessage(String message) {
        System.out.println(">> Redis로부터 수신한 메시지: " + message);
        // TODO: JSON 메시지를 파싱하여,
        // TODO: 해당 사용자에게 웹소켓으로 알림을 보내는 로직 구현
    }
}
