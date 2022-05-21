package io.slurm.kafka.lecture6;

import io.slurm.kafka.utils.GracefullyShutdownApplication;
import io.slurm.kafka.utils.JacksonConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackageClasses = {JacksonConfiguration.class, Application6.class})
public class Application6 {

	public static void main(String[] args) {
		GracefullyShutdownApplication.run(Application6.class, args);
	}

}
