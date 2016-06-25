package org.bd2k.crawler.service;

import java.util.List;

import org.bd2k.crawler.model.Page;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service("archiveService")
public class PageServiceImpl implements PageService {

	// may need refactoring
	private ApplicationContext ctx = new AnnotationConfigApplicationContext(org.bd2k.crawler.config.MongoConfig.class);
	private MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
	
	//functionality test
	public String ping() {
		System.out.println((new Page()).ping());
		return "[Archive Service Impl] I am alive";
	}

	public List<Page> getPagesByCenterID() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Page> getAllPages() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Page> getAllPagesLimOff(int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
