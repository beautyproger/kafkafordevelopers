package io.slurm.kafka.lecture6.kafka.cosumer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(WorkerProperties.PREFIX)
@Validated
public class WorkerProperties {
    public static final String PREFIX = "io.slurm.kafka.worker";

    private int treadCount = 64;

    public int getTreadCount() {
        return treadCount;
    }

    public void setTreadCount(int treadCount) {
        this.treadCount = treadCount;
    }
}
