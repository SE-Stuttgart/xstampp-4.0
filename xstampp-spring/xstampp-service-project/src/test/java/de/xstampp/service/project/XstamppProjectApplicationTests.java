package de.xstampp.service.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource( properties = { "spring.flyway.enabled=false" })
@SpringBootTest
public class XstamppProjectApplicationTests {

	@Test
	public void contextLoads() {
	}

}
