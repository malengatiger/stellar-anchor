package com.anchor.api;

import com.anchor.api.controllers.AnchorController;
import com.anchor.api.services.AccountService;
import com.anchor.api.services.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

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

	@Autowired
	private AccountService accountService;

	@Autowired
	private FirebaseService firebaseService;

	@Value("${status}")
	private String status;


	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: " +
				"ApplicationReadyEvent fired: \uD83C\uDF3C \uD83C\uDF3C app is ready to initialize Firebase .... ");

		LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: DEVELOPMENT STATUS: \uD83C\uDF51 " + status + " \uD83C\uDF51 ");
		accountService.printStellarHorizonServer();

		try {
			firebaseService.initializeFirebase();
			accountService.listenForTransactions();
			AnchorController controller = context.getBean(AnchorController.class);
			controller.getStellarToml();
		} catch (Exception e) {
			LOGGER.severe(" \uD83C\uDF45 Firebase initialization FAILED");
			e.printStackTrace();
		}
	}

}
