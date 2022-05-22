package io.slurm.kafka.lecture7.controller;

import io.slurm.kafka.lecture7.actor.RootActor;
import io.slurm.kafka.lecture7.kafka.producer.UserActionSendService;
import io.slurm.kafka.utils.kafka.UserAction;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserActionController {
	private static final Logger log = LoggerFactory.getLogger(UserActionController.class);

	private final UserActionSendService sendService;
	private final RootActor rootActor;

	public UserActionController(UserActionSendService sendService, RootActor rootActor) {
		this.sendService = sendService;
		this.rootActor = rootActor;
	}

	@PostMapping(value = "/userActionList", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void userAction(
			@RequestBody List<UserAction> actions
	) throws Exception {
		var futures = actions.stream()
				.map(userAction -> sendService.sendUserAction(userAction))
				.toArray(CompletableFuture[]::new);
		CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);
		allOf.get();
	}


	@GetMapping(value = "/actionStats", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Long> getActionStats() throws Exception {
		return rootActor.queryState();
	}
}
