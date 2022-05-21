package io.slurm.kafka.lecture6;

import io.slurm.kafka.utils.GracefullyShutdownApplication;
import io.slurm.kafka.utils.JacksonConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackageClasses = {JacksonConfiguration.class, Application5.class})
public class Application5 {

	public static void main(String[] args) {
		GracefullyShutdownApplication.run(Application5.class, args);
	}

}
