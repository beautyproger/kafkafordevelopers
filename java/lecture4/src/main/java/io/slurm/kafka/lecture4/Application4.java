package io.slurm.kafka.lecture4;

import io.slurm.kafka.utils.GracefullyShutdownApplication;
import io.slurm.kafka.utils.JacksonConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackageClasses = {JacksonConfiguration.class, Application4.class})
public class Application4 {

	public static void main(String[] args) {
		GracefullyShutdownApplication.run(Application4.class, args);
	}

}
