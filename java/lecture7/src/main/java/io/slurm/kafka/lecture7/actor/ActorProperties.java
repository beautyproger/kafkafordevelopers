package io.slurm.kafka.lecture7.actor;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(ActorProperties.PREFIX)
@Validated
public class ActorProperties {
    public static final String PREFIX = "io.slurm.kafka.event";

    private int countThreshold = 10;
    private Duration windowPeriod = Duration.ofMinutes(10);
    private Duration cleanupPeriod = Duration.ofMinutes(5);

    public int getCountThreshold() {
        return countThreshold;
    }

    public void setCountThreshold(int countThreshold) {
        this.countThreshold = countThreshold;
    }

    public Duration getWindowPeriod() {
        return windowPeriod;
    }

    public void setWindowPeriod(Duration windowPeriod) {
        this.windowPeriod = windowPeriod;
    }

    public Duration getCleanupPeriod() {
        return cleanupPeriod;
    }

    public void setCleanupPeriod(Duration cleanupPeriod) {
        this.cleanupPeriod = cleanupPeriod;
    }
}
