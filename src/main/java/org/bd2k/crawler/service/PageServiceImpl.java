package org.bd2k.crawler.service;

import java.util.List;

import org.bd2k.crawler.model.Page;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service("archiveService")
public class PageServiceImpl implements PageService {

	// may need refactoring
	private static ApplicationContext ctx = new AnnotationConfigApplicationContext(org.bd2k.crawler.config.MongoConfig.class);
	private static MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
	
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
	
	// functionality test
	public String ping() {
		System.out.println((new Page()).ping());
		return "[Archive Service Impl] I am alive";
	}
	
	public Page getPageByID(String id) {
		
		// Get requested page
		Query q = new Query(Criteria.where("id").is(id));
		Page page = mongoOperation.findOne(q, Page.class);
		
		return page;
	}

	public List<Page> getPagesByCenterID(String id) {
		
		// Retrieve from Database all archived pages for a given center id
		Query q = new Query(Criteria.where("centerID").is(id));
		List<Page> pages = mongoOperation.find(q, Page.class);
		q.with(new Sort(Sort.Direction.DESC, "lastCrawlTime"));
		
		return pages;
	}

	public List<Page> getAllPages() {
		
		// Retrieve all pages -- warning this is a dangerous function
		// as everything is returned, may want to delete and favor below
		List<Page> allPages = mongoOperation.findAll(Page.class);
		
		return allPages;
	}

	public List<Page> getAllPagesLimOff(int limit, int offset) {
		
		// Retrieve all pages with the given limit and offset
		Query q = new Query(Criteria.where("id").exists(true));
		q.skip(offset);
		q.limit(limit);
		q.with(new Sort(Sort.Direction.DESC, "lastCrawlTime"));
		List<Page> pages = mongoOperation.find(q, Page.class);
		
		return pages;
	}

	public List<Page> getPagesByCenterIDLimOff(int limit, int offset, 
			String id) {
		
		// Retrieve all pages with lim,off + centerID
		Query q = new Query(Criteria.where("centerID").is(id));
		q.limit(limit);
		q.skip(offset);
		q.with(new Sort(Sort.Direction.DESC, "lastCrawlTime"));
		List<Page> pages = mongoOperation.find(q, Page.class);
		
		return pages;
	}

	public Page getPageByURLandCenterId(String url, String id) {
		
		// Retrieve first matching page
		Query q = new Query(Criteria
				.where("url").is(url)
				.and("centerID").is(id));
		
		Page page = mongoOperation.findOne(q, Page.class); 
		
		return page;
	}
	
	public void savePage(Page p) {
		
		mongoOperation.save(p);
	}

	public void saveOrUpdatePage(Page p) {
		
		// Save the page if it does not exist, else update
		Page check = getPageByURLandCenterId(p.getUrl(), p.getCenterID());
		
		//if the page exists in DB already
		if(check != null) {
			check.setCurrentContent(p.getCurrentContent());
			check.setLastCrawlTime(p.getLastCrawlTime());
			check.setLastDiff(p.getLastDiff());
			mongoOperation.save(check);
		}
		
		//else insert page normally
		else {
			mongoOperation.save(p);
		}
		
	}
	
	
}
