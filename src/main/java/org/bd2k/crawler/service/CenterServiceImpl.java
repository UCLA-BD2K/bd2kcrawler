package org.bd2k.crawler.service;

import java.util.List;

import org.bd2k.crawler.model.Center;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Implementation of service to handle requests for Centers collection.
 * @author allengong
 *
 */
@Service("centerService")
public class CenterServiceImpl implements CenterService {
	
	// may need refactoring
	private ApplicationContext ctx = new AnnotationConfigApplicationContext(org.bd2k.crawler.config.MongoConfig.class);
	private MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

	public Center getCenterByID(String id) {
		
		return mongoOperation.findOne(new Query(Criteria.where("centerID").is(id)), Center.class);
	}

	/**
	 * gets all centers
	 */
	public List<Center> getAllCenters() {
		
		// get all of the centers
		List<Center> centers = mongoOperation.findAll(Center.class);
		return centers;
	}
}
