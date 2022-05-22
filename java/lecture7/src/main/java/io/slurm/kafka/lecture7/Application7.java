package io.slurm.kafka.lecture7;

import io.slurm.kafka.utils.GracefullyShutdownApplication;
import io.slurm.kafka.utils.JacksonConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackageClasses = {JacksonConfiguration.class, Application7.class})
public class Application7 {

	public static void main(String[] args) {
		GracefullyShutdownApplication.run(Application7.class, args);
	}

}
