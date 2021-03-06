package org.bd2k.crawler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@PropertySource("classpath:app.properties")
public class MongoConfig extends AbstractMongoConfiguration {
	
	@Value("${db.host}")
	String dbHost;
	
	@Value("${db.dbname}")
	String dbname;
	
	/**
	 * Singleton instance of MongoClient
	 */
	private static Mongo mongoClient;
	
	@Override
	public String getDatabaseName() {
		return dbname;
	}
	
	@Override
	@Bean
	public Mongo mongo() throws Exception {
		
		if (mongoClient == null) {
			mongoClient = new MongoClient(dbHost);
		}
		
		return mongoClient; 
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	   return new PropertySourcesPlaceholderConfigurer();
	}
}
