package com.interactions;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FormgenApplication.class)
@WebAppConfiguration
public class FormgenApplicationTests {

	@Test
	public void contextLoads() throws IOException {
		
		 Resource resource = new ClassPathResource("/xml/formFillingTemplate.xml");
		 System.out.println(resource.getFile());
	}

}
