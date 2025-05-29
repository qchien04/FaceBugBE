package com;

import com.entity.chatrealtime.Message;
import com.service.chatrealtime.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocialNetworkApplication {
	@Autowired
	private MessageService service;
	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(){
		return runner->{
			System.out.println("DONE");
//			Message message=new Message();
//			message.setContent("Start");
//			message.setMessageType(Message.MessageType.TEXT);
//			service.createMessage(message);




		};
	}
}
