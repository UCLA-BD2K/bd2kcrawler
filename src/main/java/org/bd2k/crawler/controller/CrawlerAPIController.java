package org.bd2k.crawler.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bd2k.crawler.crawler.BD2KCrawler;
import org.bd2k.crawler.crawler.BD2KPubCrawler;
import org.bd2k.crawler.crawler.Digester;
import org.bd2k.crawler.crawler.Email;
import org.bd2k.crawler.model.Center;
import org.bd2k.crawler.model.Page;
import org.bd2k.crawler.service.CenterService;
import org.bd2k.crawler.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
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
	private PageService pageService;
	
	@Autowired
	private CenterService centerService;
	
	//single instance crawler
	private BD2KCrawler crawler;
		
	/* sanity check routes */
	@Value("${db.host}") 
	String val;
	
	@RequestMapping(value="/testValue")
	public String getValue() {
		
		Digester dig = new Digester("hello worldsewoo", "hello my world, whoa somethign new");
		//System.out.println(dig.computeSemanticDiff());
		
		//return val;
		List<Center> centers = centerService.getAllCenters();
		System.out.println("got the centers");
		for(Center c : centers)
			System.out.println("[Center]: " + c.getCenterID());
		
		
		return dig.computeHTMLDiff();
	}
	
	
	@RequestMapping(value="/testGet")
	public Page getPageFromDB(@RequestParam("url") String url) {
		
		//move below to service
		ApplicationContext ctx = new AnnotationConfigApplicationContext(org.bd2k.crawler.config.MongoConfig.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

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
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String crawlStartTime = df.format(new Date());
		
		crawler = new BD2KCrawler("TestCenter", "https://bd2kccc.org", seedURLs,
				excludedURLs);

		crawler.setSeedURLs(seedURLs);
		
		try {
			
			Map<String, String> results = BD2KCrawler.crawl();
			List<String> recipients = new ArrayList<String>();
			recipients.add("alm.gong@gmail.com");
			
			String subject = "[BD2K Crawler] Results for crawl";
			String header = "Request /testCrawl?url=" + url + "\n";
			header += "Crawl initiated on: " + crawlStartTime + "\n";
			header += "\n--------Center: " + "TestCenter--------\n";
			String body = header + formatCrawlResultsForEmail(results);
			
			String attachment = "";
			
			sendCrawlResultsEmail(
					subject, body, attachment, recipients);
			
			//Email.send(properties, recipients, subject, body, attachment);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("error in crawl()");
			e.printStackTrace();
		}
		
		return "everything went OK";
	}
		
	@RequestMapping(value="/crawlStop")
	public String testStopCrawler() {
		boolean stopped = crawler.stopCrawling();	//may need crawler to be a static member
		
		return "Crawler gracefully stopped: " + stopped;
	}
		
	@RequestMapping(value="/testPages")
	public List<Page> testPages() {
		
		Page p = pageService.getPageByURLandCenterId("googles.com", null);
		if(p == null)
			System.out.println("doesnt exist...");
		
		return pageService.getAllPages();
		//return pageService.getPageByURLandCenterId("google.com", null);
		//return pageService.getPageByID("57717eaa83e10e0750c58ca7");
	}
	
	@RequestMapping(value="/testGetStatus")
	public String testStatus() {
		
		if(crawler == null) {
			return "[ OK ]: Crawler is idle";
		}
		
		int status = crawler.getCrawlerStatus();
		if(status == 1) {
			return "[ ! ] Crawler is already running, please wait until it completes before initiating another crawl";
		}
		
		return "[ OK ]: Crawler is idle.";
		
	}
	
	@RequestMapping(value="/testPub")
	public String testPub() {
		
		String[] centers = {"LINCS-DCIC"};
		BD2KPubCrawler.setCentersToCrawl(centers);
		Map<String, String> results = new HashMap<String,String>();
		try {
			 results = BD2KPubCrawler.crawl();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if(results == null) {
			return "crawler running";
		}
		
		String ret = "";
		for(Map.Entry<String, String> entry : results.entrySet()) {
			ret+=(entry.getKey() + " " + entry.getValue() + "\n");
		}
		
		return ret;
	}
		
	
	/* Helpers */
	public void sendCrawlResultsEmail(
			String subject, String body, String attachment, 
			List<String> recipients) {
	
		try {
			Resource resource = new ClassPathResource("credentials.properties");
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			
			Email.send(properties, recipients, subject, body, attachment);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public String formatCrawlResultsForEmail(Map<String, String> results) {
		
		String formattedString = "\n";
		for(Map.Entry<String, String> entry : results.entrySet()) {
			//formattedString += 
			//		("<a href=\"http://127.0.0.1:8080/BD2KCrawler/digestResults?id=" + 
			//				entry.getValue() + "\">" + entry.getKey() + "</a><br/>");
			
			formattedString += entry.getKey() + " --> " +
								"http://127.0.0.1:8080/BD2KCrawler/digestResults?id=" + 
								entry.getValue() + "\n\n";
		}
		
		return formattedString;
	}	
}
