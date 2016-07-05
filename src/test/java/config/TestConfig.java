package config;

import org.bd2k.crawler.service.CenterService;
import org.bd2k.crawler.service.CenterServiceImpl;
import org.bd2k.crawler.service.PageService;
import org.bd2k.crawler.service.PageServiceImpl;
import org.bd2k.crawler.service.PublicationService;
import org.bd2k.crawler.service.PublicationServiceImpl;
import org.bd2k.crawler.service.UserService;
import org.bd2k.crawler.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config file to be used in conjunction with unit tests.
 * In general, this file will help with autowiring members
 * in tests via configuring beans.
 * 
 * Tests run with JUnit.
 * 
 * @author allengong
 *
 */
@Configuration
public class TestConfig {
	
	/* Page related beans */
	
	@Bean
	PageService pageService() {
		return new PageServiceImpl();
	}
	
	
	/* Center related beans */
	
	@Bean
	CenterService centerService() {
		return new CenterServiceImpl();
	}
	
	
	/* Publication related beans */
	
	PublicationService publicationService() {
		return new PublicationServiceImpl();
	}
	
	
	/* User service beans */
	
	UserService userService() {
		return new UserServiceImpl();
	}

}
