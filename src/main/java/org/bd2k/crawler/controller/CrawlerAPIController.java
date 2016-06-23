package org.bd2k.crawler.controller;

import org.bd2k.crawler.model.Page;
import org.bd2k.crawler.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for making external API requests. Primarily a test 
 * controller for interal use.
 * @author allengong
 *
 */
@RestController
public class CrawlerAPIController {
	
	//private BD2KCrawler crawler;	//singleton instance used to call crawl ops, probably belongs in service
	
	@Autowired
	private PageService pageServce;
	
	/* sanity check routes */
	
	@RequestMapping(value="/testInsert")
	public Page insertIntoDB(@RequestParam("url")String url) {
		//repo.save(new Page(url));
		return new Page();
	}
	
	@RequestMapping(value="/testGet")
	public Page getPageFromDB(@RequestParam("url") String url) {
		
		//move below to service
		ApplicationContext ctx = new AnnotationConfigApplicationContext(org.bd2k.crawler.config.MongoConfig.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		//System.out.println("inserting facebook");
//		Page dummyPage = new Page("facebook.com");
//		mongoOperation.save(dummyPage);

		Query q = new Query(Criteria.where("url").is(url));
		Page p = mongoOperation.findOne(q, Page.class);
		
		((AbstractApplicationContext) ctx).close();
		return p;
	}
}
