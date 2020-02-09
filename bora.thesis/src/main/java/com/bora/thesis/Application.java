package com.bora.thesis;

import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;

/**
 * @author bora
 */
@SpringBootApplication(exclude = { ErrorMvcAutoConfiguration.class, SecurityAutoConfiguration.class })
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	protected void addAdditionalDialects(final Set<IDialect> dialects) {
		dialects.add(new SpringSecurityDialect());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
