package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * Copy of MongoConfig, but with properties found in the app-test
 * file. All test cases should utilize the members in this class,
 * as to not affect values stored in the production database (see app.properties).
 * 
 * @author allengong
 *
 */
@Configuration
@PropertySource("classpath:app-test.properties")	// different
public class MongoConfigTest extends AbstractMongoConfiguration {

	@Value("${db.host}")
	String dbHost;
	
	@Value("${db.dbname}")
	String dbname;
	
	@Override
	public String getDatabaseName() {
		return dbname;
	}
	
	@Override
	@Bean
	public Mongo mongo() throws Exception {
		return new MongoClient(dbHost); 
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	   return new PropertySourcesPlaceholderConfigurer();
	}
	
}
