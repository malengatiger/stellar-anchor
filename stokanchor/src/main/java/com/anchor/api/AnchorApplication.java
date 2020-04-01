package com.anchor.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
import java.util.logging.Logger;

@SpringBootApplication
public class AnchorApplication implements ApplicationListener<ApplicationReadyEvent> {
	public static final Logger LOGGER = Logger.getLogger(AnchorApplication.class.getSimpleName());

	public static void main(String[] args) {
		LOGGER.info("\uD83C\uDF51 \uD83C\uDF51 \uD83C\uDF51 AnchorApplication starting ...");
		SpringApplication.run(AnchorApplication.class, args);
		LOGGER.info("\uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C AnchorApplication started ..." +
				" \uD83E\uDD6C \uD83E\uDD6C \uD83E\uDD6C");
	}
	@Autowired
	private ApplicationContext context;
	@Value("${status}")
	private String status;


	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: " +
				"ApplicationReadyEvent fired: \uD83C\uDF3C \uD83C\uDF3C app is ready to initialize Firebase .... ");

		FirebaseScaffold scaffold = context.getBean(FirebaseScaffold.class);
		try {
			scaffold.initializeFirebase();
		} catch (Exception e) {
			LOGGER.severe("Firebase initialization FAILED");
			e.printStackTrace();
		}
	}

}
