package de.xstampp.service.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Excluding {@link HibernateJpaAutoConfiguration} because we use the Hibernate
 * framework directly and are not supposed to replace it with another JPA
 * implementation.
 */
@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
public class XstamppAuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(XstamppAuthenticationApplication.class, args);
	}
}
