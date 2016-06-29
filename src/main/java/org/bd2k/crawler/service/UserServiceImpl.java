package org.bd2k.crawler.service;

import org.bd2k.crawler.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	// may need refactoring
	private static ApplicationContext ctx = new AnnotationConfigApplicationContext(org.bd2k.crawler.config.MongoConfig.class);
	private static MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

	public User getUserByEmail(String email) {
		
		Query q = new Query(Criteria.where("email").is(email));
		User u = mongoOperation.findOne(q, User.class);
		
		return u;
	}

}
