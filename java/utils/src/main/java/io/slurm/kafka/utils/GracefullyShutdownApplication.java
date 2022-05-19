package io.slurm.kafka.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class GracefullyShutdownApplication extends SpringApplication {
    private static final Logger log = LoggerFactory.getLogger(GracefullyShutdownApplication.class);
    private final AtomicBoolean stopFlag = new AtomicBoolean(false);

    public GracefullyShutdownApplication(Class<?>... primarySources) {
        super(primarySources);
        setRegisterShutdownHook(false);
    }

    @Override
    protected void refresh(ConfigurableApplicationContext applicationContext) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> gracefullyShutdown(applicationContext)));
        super.refresh(applicationContext);
        stopFlag.set(true);
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return new GracefullyShutdownApplication(primarySource).run(args);
    }

    public void gracefullyShutdown(ConfigurableApplicationContext applicationContext) {
        if (!stopFlag.compareAndSet(true, false)) {
            return;
        }
        log.info("Stopping context...");
        applicationContext.publishEvent(new GracefullyShutdownStartEvent(this));
        //Duration preShutdownTimeout = settings.getPreShutdownTimeout();
        Duration preShutdownTimeout = Duration.ofSeconds(5);
        if (preShutdownTimeout != null && preShutdownTimeout.toMillis() > 0) {
            Instant shutdownTime = Instant.now().plus(preShutdownTimeout);
            try {
                Duration timeToStop = Duration.between(Instant.now(), shutdownTime);
                while (timeToStop.toMillis() > 0) {
                    log.info("{} second left before shutdown...", timeToStop.getSeconds());
                    Thread.sleep(Math.min(timeToStop.toMillis(), Duration.ofSeconds(1).toMillis()));
                    timeToStop = Duration.between(Instant.now(), shutdownTime);
                }
            } catch (InterruptedException ex) {
                log.warn("Wait interrupt", ex);
                Thread.currentThread().interrupt();
            }
        }
        log.info("Shutdown all services ...");
        applicationContext.publishEvent(new GracefullyShutdownFinish(this));
        applicationContext.close();
    }
}
