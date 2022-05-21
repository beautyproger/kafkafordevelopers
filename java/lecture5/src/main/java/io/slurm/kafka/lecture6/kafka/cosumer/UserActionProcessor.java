package io.slurm.kafka.lecture6.kafka.cosumer;

import io.slurm.kafka.utils.kafka.UserAction;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserActionProcessor {
    private static final Logger log = LoggerFactory.getLogger(UserActionProcessor.class);

    public void processUserAction(UserAction action) throws InterruptedException {
        if (ThreadLocalRandom.current().nextDouble() < 0.1) {
            Thread.sleep(400);
        } else {
            Thread.sleep(100);
        }
        log.info("User action: {}", action);
    }
}
