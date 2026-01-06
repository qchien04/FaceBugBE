//package com.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.KafkaOperations;
//import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
//import org.springframework.kafka.listener.DefaultErrorHandler;
//import org.springframework.util.backoff.FixedBackOff;
//
//@Configuration
//@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
//public class KafkaConfig {
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
//            ConsumerFactory<Object, Object> consumerFactory,
//            KafkaOperations<Object, Object> kafkaOperations
//    ) {
//        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//
//        // Tạo DeadLetterPublishingRecoverer và policy retry
//        DeadLetterPublishingRecoverer recoverer =
//                new DeadLetterPublishingRecoverer(kafkaOperations);
//        FixedBackOff backOff = new FixedBackOff(1_000L /* 1 giây */, 2 /* retry 2 lần */);
//        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
//
//        factory.setCommonErrorHandler(errorHandler);
//        return factory;
//    }
//
//    // Định nghĩa topic chính và DLT
//    @Bean
//    public NewTopic notification() {
//        return new NewTopic("Notification", 2, (short) 1);
//    }
//
//    @Bean
//    public NewTopic notificationDlt() {
//        return new NewTopic("Notification-dlt", 1, (short) 1);
//    }
//}
