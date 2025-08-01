package com.controller;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/public")
@AllArgsConstructor
public class PublicController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping
    public ResponseEntity<Void> sendHello() {
        kafkaTemplate.send("Notification", "Hello world")
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        System.out.println("Gửi thành công, offset=" +
                                res.getRecordMetadata().offset());
                    } else {
                        System.err.println("Gửi thất bại: " + ex.getMessage());
                    }
                });
        return ResponseEntity.accepted().build();
    }

    @KafkaListener(id = "notificationGroup", topics = "Notification")
    public void listen(String message) {
        System.out.println("Nhận: " + message);
        throw new RuntimeException("Kích hoạt DLT");
    }

    @KafkaListener(id = "dltGroup3", topics = "Notification-dlt")
    public void dltListen(ConsumerRecord<?, ?> record) {
        System.out.println("=== Message từ DLT ===");
        System.out.println("Value   = " + record.value());
        System.out.println("Headers = " + record.headers());
    }
}

