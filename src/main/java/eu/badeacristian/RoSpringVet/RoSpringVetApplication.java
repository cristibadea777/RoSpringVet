package eu.badeacristian.RoSpringVet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories({"eu.badeacristian.RoSpringVet.repositories", "eu.badeacristian.RoSpringVet.services"})
@EntityScan({"eu.badeacristian.RoSpringVet.models"})
@ComponentScan({"eu.badeacristian.RoSpringVet", "eu.badeacristian.RoSpringVet.controllers"})
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class})

//
public class RoSpringVetApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RoSpringVetApplication.class, args);
	}
	
}
