package com.example.bot_task_etc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BotTaskEtcApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotTaskEtcApplication.class, args);
	}

}
