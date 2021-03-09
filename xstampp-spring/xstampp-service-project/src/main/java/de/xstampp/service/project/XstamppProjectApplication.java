package de.xstampp.service.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Excluding {@link HibernateJpaAutoConfiguration} because we use the Hibernate framework directly and are not supposed
 * to replace it with another JPA implementation.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude=HibernateJpaAutoConfiguration.class)
public class XstamppProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(XstamppProjectApplication.class, args);
	}
}
