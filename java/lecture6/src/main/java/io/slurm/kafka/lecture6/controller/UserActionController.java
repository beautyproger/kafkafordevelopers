package io.slurm.kafka.lecture6.controller;

import io.slurm.kafka.lecture6.database.EmulatedRepository;
import io.slurm.kafka.lecture6.kafka.producer.UserActionSendService;
import io.slurm.kafka.utils.kafka.UserAction;
import java.util.List;
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
	private final EmulatedRepository repository;

	public UserActionController(UserActionSendService sendService, EmulatedRepository repository) {
		this.sendService = sendService;
		this.repository = repository;
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
	public EmulatedRepository.DatabaseState getActionStats() throws Exception {
		return repository.queryState();
	}
}
