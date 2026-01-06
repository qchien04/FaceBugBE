package com;

//import com.entity.chatrealtime.Message;
import com.service.chatrealtime.MessageService;
//import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.core.KafkaOperations;
//import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
//import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.util.backoff.FixedBackOff;

@SpringBootApplication
@EnableScheduling
//@EnableKafka
public class SocialNetworkApplication {
	@Autowired
	private MessageService service;
	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(){
		return runner->{
			System.out.println("DONE HEHE");
		};
	}
	// Xử lý khi mà consumer (chính mình) kéo message về và xử lý trong listener,trong hàm đó nếu ném ra exception
	// thì thực hiện lại hàm 1 số lần, nếu không được thì gửi vào dlt
//	@Bean
//	DefaultErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
//		return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2));
//	}
}
