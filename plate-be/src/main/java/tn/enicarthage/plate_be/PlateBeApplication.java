package tn.enicarthage.plate_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PlateBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlateBeApplication.class, args);
	}

}
