package com.anchor.api;

import com.anchor.api.controllers.AnchorController;
import com.anchor.api.services.AccountService;
import com.anchor.api.services.FirebaseService;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.ImageBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Calendar;

@SpringBootApplication
@EnableScheduling
public class AnchorApplication implements ApplicationListener<ApplicationReadyEvent> {
	public static final Logger LOGGER = LoggerFactory.getLogger(AnchorApplication.class.getSimpleName());
	private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	public static void main(String[] args) {
		LOGGER.info(Emoji.PANDA.concat(Emoji.PANDA).concat(Emoji.PANDA) +
				" AnchorApplication starting ...");
		SpringApplication app = new SpringApplication(AnchorApplication.class);
		app.setLogStartupInfo(true);
		app.setBanner(new Banner() {
			@Override
			public void printBanner (Environment environment,
									 Class<?> sourceClass,
									 PrintStream out) {
				//LOGGER.info(G.toJson(environment.getActiveProfiles()));
				out.println(getBanner());
			}
		});

		app.run(args);
		LOGGER.info(Emoji.PANDA.concat(Emoji.PANDA).concat(Emoji.PANDA) +
				" AnchorApplication started OK! ".concat(Emoji.HAND2.concat(Emoji.HAND2))
				+ " All services up and running.");
	}
	private static String getBanner() {
		StringBuilder sb = new StringBuilder();
		sb.append("###############################################\n");
		sb.append("#### "+Emoji.HEART_BLUE+"ANCHOR BANK NETWORK SERVICES "+Emoji.HEART_BLUE+"   ####\n");
		sb.append("#### ".concat(Emoji.FLOWER_RED).concat(" ").concat(new DateTime().toDateTimeISO().toString().concat("     ####\n")));
		sb.append("###############################################\n");
		return sb.toString();
	}
	@Autowired
	private FirebaseService firebaseService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private ApplicationContext context;
	@Value("${status}")
	private String status;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: " +
				"ApplicationReadyEvent fired: \uD83C\uDF3C \uD83C\uDF3C app is ready to initialize Firebase .... ");
		LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: DEVELOPMENT STATUS: " +
				"\uD83C\uDF51 " + status + " \uD83C\uDF51 ");
		LOGGER.info("\uD83C\uDF3C \uD83C\uDF3C onApplicationEvent: DEVELOPMENT STATUS: \uD83C\uDF51 " + status + " \uD83C\uDF51 ");
		accountService.printStellarHorizonServer();

		Calendar cal = Calendar.getInstance();
		int res = cal.getActualMaximum(Calendar.DATE);
		LOGGER.info(Emoji.SKULL.concat(Emoji.SKULL) + "Today's Date = " + cal.getTime());
		LOGGER.info(Emoji.SKULL.concat(Emoji.SKULL) + "Last Date of the current month = " + res);

		try {
			firebaseService.initializeDatabase();
			accountService.listenForTransactions();
			AnchorController controller = context.getBean(AnchorController.class);
			controller.getStellarToml();
		} catch (Exception e) {
			LOGGER.info(" \uD83C\uDF45 Firebase initialization FAILED");
			e.printStackTrace();
		}
	}
}
//11140
