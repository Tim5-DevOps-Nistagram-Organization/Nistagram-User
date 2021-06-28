package rs.ac.uns.ftn.devops.tim5.nistagramuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NistagramUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(NistagramUserApplication.class, args);
	}

}
