package io.slurm.kafka.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

public class GracefullyShutdownFinish extends ApplicationEvent {
    private static final Logger log = LoggerFactory.getLogger(GracefullyShutdownFinish.class);

    public GracefullyShutdownFinish(Object source) {
        super(source);
    }
}
