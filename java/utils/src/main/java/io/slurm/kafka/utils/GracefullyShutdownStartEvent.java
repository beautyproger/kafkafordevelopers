package io.slurm.kafka.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

public class GracefullyShutdownStartEvent extends ApplicationEvent {
    private static final Logger log = LoggerFactory.getLogger(GracefullyShutdownStartEvent.class);

    public GracefullyShutdownStartEvent(Object source) {
        super(source);
    }
}
