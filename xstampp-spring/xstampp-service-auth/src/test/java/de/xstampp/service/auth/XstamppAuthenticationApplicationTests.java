package de.xstampp.service.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource( properties = { "spring.flyway.enabled=false" })
@SpringBootTest
public class XstamppAuthenticationApplicationTests {

	@Test
	public void contextLoads() {
	}

}
