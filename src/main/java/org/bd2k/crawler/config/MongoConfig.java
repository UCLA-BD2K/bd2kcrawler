package org.bd2k.crawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {
	
	@Override
	public String getDatabaseName() {
		return "BD2KCrawlerDB";
	}
	
	@Override
	@Bean
	public Mongo mongo() throws Exception {
		return new MongoClient("localhost");
	}
}
