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

	// for dynamically setting config for an instance of CenterService, useful for testing
	public void setMongoConfigContext(String cls) {
		try {
			Class className = Class.forName(cls);
			ctx = new AnnotationConfigApplicationContext(className);
			mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		}
		catch(ClassNotFoundException e) {
			// if exception, do nothing
			e.printStackTrace();
		}
	}
	
	public Center getCenterByID(String id) {
		
		return mongoOperation.findOne(new Query(Criteria.where("centerID").is(id)), Center.class);
	}

	/*
	 * gets all centers
	 */
	public List<Center> getAllCenters() {
		
		// get all of the centers
		List<Center> centers = mongoOperation.findAll(Center.class);
		return centers;
	}
	
	/*
	 * Save or update center, not exposed by interface since the 
	 * center information is never modified after creation. 
	 */
	public void saveOrUpdateCenter(Center c) {
		
		Query q = new Query(Criteria.where("centerID").is(c.getCenterID()));
		Center check = mongoOperation.findOne(q, Center.class);
		
		if(check != null) {
			check.setCenterID(c.getCenterID());
			check.setGrant(c.getGrant());
			check.setSiteURL(c.getSiteURL());
			
			mongoOperation.save(check);
		}
		else {
			mongoOperation.save(c);
		}
	}
}
