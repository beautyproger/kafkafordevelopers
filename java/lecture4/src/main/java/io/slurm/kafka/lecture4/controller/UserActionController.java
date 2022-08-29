package io.slurm.kafka.lecture4.controller;

import io.slurm.kafka.lecture4.kafka.producer.UserActionSendService;
import io.slurm.kafka.utils.kafka.UserAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserActionController {
	private static final Logger log = LoggerFactory.getLogger(UserActionController.class);

	private final UserActionSendService sendService;

	public UserActionController(UserActionSendService sendService) {
		this.sendService = sendService;
	}

	@PostMapping(value = "/userAction", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void userAction(
			@RequestBody UserAction userAction
	) {
		sendService.sendUserAction(userAction);
	}
}
