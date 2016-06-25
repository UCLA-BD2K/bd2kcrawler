package org.bd2k.crawler.controller;

import java.util.List;

import org.bd2k.crawler.crawler.BD2KCrawler;
import org.bd2k.crawler.crawler.Digester;
import org.bd2k.crawler.model.Center;
import org.bd2k.crawler.model.Page;
import org.bd2k.crawler.service.CenterService;
import org.bd2k.crawler.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * controller for internal use.
 * @author allengong
 *
 */
@RestController
public class CrawlerAPIController {
	
	//private BD2KCrawler crawler;	//singleton instance used to call crawl ops, probably belongs in service
	
	@Autowired
	private PageService pageServce;
	
	@Autowired
	private CenterService centerService;
	
	//single instance crawler
	private BD2KCrawler crawler;
		
	/* sanity check routes */
	@Value("${db.host}") 
	String val;
	
	@RequestMapping(value="/testValue")
	public String getValue() {
		
		Digester dig = new Digester("hello worlds", "hello my world");
		//System.out.println(dig.computeSemanticDiff());
		
		//return val;
		List<Center> centers = centerService.getAllCenters();
		System.out.println("got the centers");
		for(Center c : centers)
			System.out.println("[Center]: " + c.getCenterID());
		
		
		return dig.computeHTMLDiff();
	}
	
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
	
	@RequestMapping(value="/testCrawl")
	public String crawlPage(@RequestParam("url") String url) {
		//BD2KCrawler crawler = new BD2KCrawler();
		String[] seedURLs = {url};
		String[] excludedURLs = {};
		crawler = new BD2KCrawler("", "https://bd2kccc.org", seedURLs,
				excludedURLs);
		crawler.setDomain("https://bd2kccc.org");
		crawler.setSeedURLs(seedURLs);
		
		try {
			crawler.crawl();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("error in crawl()");
			e.printStackTrace();
		}
		
		return "everything went OK";
	}
	
	@RequestMapping(value="/testCrawl/stop")
	public String testStopCrawler() {
		crawler.stopCrawling();	//may need crawler to be a static member
		
		return "crawler gracefully stopped.";
	}
}
