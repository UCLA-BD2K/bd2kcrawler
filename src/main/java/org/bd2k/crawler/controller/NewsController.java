package org.bd2k.crawler.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.bd2k.crawler.crawler.BD2KCrawler;
import org.bd2k.crawler.crawler.Email;
import org.bd2k.crawler.model.Center;
import org.bd2k.crawler.model.Page;
import org.bd2k.crawler.service.CenterService;
import org.bd2k.crawler.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for /news, may initiate crawling.
 * @author allengong
 *
 */
@RestController
public class NewsController {
	
	@Autowired
	private PageService pageService;
	
	@Autowired
	private CenterService centerService;
	
	@Value("${email.recipients}")
	private String emailRecipients;		// comma separated emails (no spaces)
	
	// class crawler
	private static BD2KCrawler crawler;
	
	private final int CRAWLER_RUNNING = 1;
	private final int CRAWLER_IDLE = 0;
	private final String host = "127.0.0.1:8080/BD2KCrawler";	// for email, change to domain
		
	/**
	 * Checks all websites and checks if there are any changes since the last check. 
	 * If the process is already running, it should return the status.
	 */
	@RequestMapping(value="/news/update", method=RequestMethod.GET)
	public String getNewCrawlData(HttpServletResponse res) {
		
		System.out.println("running crawler on all sites...");
		
		List<Center> centers = centerService.getAllCenters();
		String[] seeds = new String[centers.size()];
		String[] excludes = {};
		
		Map<String, String> crawlerResults = null;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String crawlStartTime = df.format(new Date());
		
		for (int i = 0; i < seeds.length; i++) {
			seeds[i] = centers.get(i).getSiteURL();
		}
		
		//if no crawler instance
		if(crawler == null) {
			crawler = new BD2KCrawler();
		}
		
		if(crawler.getCrawlerStatus() == CRAWLER_IDLE) {
			
			// generate email contents
			List<String> recipients = new ArrayList<String>();
			
			String[] recipientArr = emailRecipients.split(",");
			for(int i = 0 ; i < recipientArr.length; i++) {
				recipients.add(recipientArr[i]);
			}
			
			String subject = "[BD2K Crawler] Results for crawl";
			String header = "Request /news/update\n";
			header += "Crawl initiated on: " + crawlStartTime + "\n";
			
			String body = header;	// start with the header
			String attachment = "";
			
			//if crawler is idle, then iteratively crawl all centers
			for (int i = 0; i < seeds.length; i++) {
				
				String[] currSeed = {seeds[i]};			//for each site
				
				//current crawler info
				crawler = new BD2KCrawler(
						centers.get(i).getCenterID(), 
						seeds[i], 
						currSeed, 
						excludes);
				
				try {
					
					crawlerResults = BD2KCrawler.crawl();	// blocks here until crawl complete
					
					// add to the email body
					body += "\n--------Center: " + crawler.getCenterID() + "--------\n";
					body += formatCrawlResultsForEmail(crawlerResults); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// now actually send the huge email
			sendCrawlResultsEmail(
					subject, body, attachment, recipients);
		} else {
			return "[ ! ]: Crawler already running, please try again later.";
		}
		
		return "[ OK ]: Crawl complete.";
	}
	
	/**
	 * Only checks the website associated with {id}.
	 */
	@RequestMapping(value="/news/update/{id}", method=RequestMethod.GET) 
	public String getNewCrawlDataForId(HttpServletResponse res, 
			@PathVariable("id") String id) {
		
		System.out.println("running crawler for centerID: " + id);
		
		List<Center> centers = centerService.getAllCenters();
		String seedURL = null;
		for (Center c : centers) {
			System.out.println(c.getCenterID());
			if (c.getCenterID().equals(id)) {
				seedURL = c.getSiteURL();
				break;
			}
		}
		
		// if a valid center id, proceed
		if(seedURL!=null) {
			String[] seeds = {seedURL};
			String[] excludes = {};
			Map<String, String> crawlerResults = null;
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			String crawlStartTime = df.format(new Date());
			
			// crawler can be null if no other crawls started
			if (crawler == null) {
				crawler = new BD2KCrawler(id, seedURL, seeds, excludes);
				
				// Crawler is running!!!
				if (crawler.getCrawlerStatus() == CRAWLER_RUNNING) {
					return "[ ! ]: Crawler is already running, please try again later.";
				}
				
				// attempt to crawl
				try {
					crawlerResults = BD2KCrawler.crawl();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (crawler.getCrawlerStatus() ==  CRAWLER_RUNNING) {
				return "[ ! ]: Crawler is already running, please try again later.";
			} else {
				// crawler exists and is idle, so just run it
				crawler = new BD2KCrawler(id, seedURL, seeds, excludes);
				try {
					crawlerResults = BD2KCrawler.crawl();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// generate email contents
			List<String> recipients = new ArrayList<String>();
			
			String[] recipientArr = emailRecipients.split(",");
			for(int i = 0 ; i < recipientArr.length; i++) {
				recipients.add(recipientArr[i]);
			}
			
			String subject = "[BD2K Crawler] Results for crawl";
			String header = "Request /news/update/" + id + "\n";
			header += "Crawl initiated on: " + crawlStartTime + "\n";
			header += "\n--------Center: " + id + "--------\n";
			String body;
			String attachment = "";
			
			// generate remaining part of email body
			body = header + formatCrawlResultsForEmail(crawlerResults);
			
			sendCrawlResultsEmail(
					subject, body, attachment, recipients);
		}
		else {
			System.out.println("No matching id");
		}
		
		
		return "[ OK ]: Crawl complete.";
	}
	
	/**
	 * Allows the viewers to see what the diffs are from all websites.
	 */
	@RequestMapping(value="/news/changes", method=RequestMethod.GET) 
	public List<Page> getNewChanges(HttpServletResponse res,
			@RequestParam(value="limit", required=false) Integer limit,
			@RequestParam(value="offset", required=false) Integer offset) {
		
		if (limit != null && offset != null) {
			return pageService.getAllPagesLimOff(limit, offset);
		}
		
		return pageService.getAllPages();
	}
	
	/**
	 * Allows the viewers to see what the diffs are from the website associated with [id].
	 */
	@RequestMapping(value="/news/changes/{id}", method=RequestMethod.GET) 
	public List<Page> getNewChanges(HttpServletResponse res, 
			@PathVariable("id") String id,
			@RequestParam(value="limit", required=false) Integer limit,
			@RequestParam(value="offset", required=false) Integer offset) {
			
		if (limit != null && offset != null) {
			return pageService.getPagesByCenterIDLimOff(limit, offset, id);
		}
		
		return pageService.getPagesByCenterID(id);
	}
	
	/**
	 * Allows viewers to see the status of the crawler
	 */
	@RequestMapping(value="/news/crawlerStatus")
	public String getCrawlerStatus() {

		int status = new BD2KCrawler().getCrawlerStatus();
		
		if (status == CRAWLER_RUNNING) {
			return "Crawler is currently running...";
		}
		
		return "Crawler is idle.";
		
	}
	
	/**
	 * Allows moderators to request that the crawler be stopped.
	 */
	@RequestMapping(value="/news/crawlerStop")
	public String stopCrawler() {
		
		return "Crawler stopped: " + BD2KCrawler.stopCrawling();
	}
	
	/**
	 * Retrieves ALL pages - this is a dangerous function.
	 */
	@RequestMapping(value="/news/getAllPages")
	public List<Page> getAllPages() {
		
		return pageService.getAllPages();
	}
	
	/**
	 * Retrieve all pages with a specified limit and offset.
	 */
	@RequestMapping(value="/news/getPages")
	public List<Page> getAllPagesLimOff(@RequestParam("limit") int limit,
			@RequestParam("offset") int offset) {
		
		return pageService.getAllPagesLimOff(limit, offset);
	}
	
	/**
	 * Retrieve pages for a given center and limit + offset
	 */
	@RequestMapping(value="/news/getPagesForCenter")
	public List<Page> getPagesForCenter(@RequestParam("center") String id) {
		
		return pageService.getPagesByCenterIDLimOff(10,10 ,id);
	}
	
	/* Private helpers */
	
	private void sendCrawlResultsEmail(
			String subject, String body, String attachment, 
			List<String> recipients) {
	
		try {
			Resource resource = new ClassPathResource("credentials.properties");
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			
			Email.send(properties, recipients, subject, body, attachment);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private String formatCrawlResultsForEmail(Map<String, String> results) {
			
		String formattedString = "\n";
		
		if (results.isEmpty()) {
			return formattedString + "NO CHANGES\n\n";
		}
		
		for (Map.Entry<String, String> entry : results.entrySet()) {
		
			formattedString += entry.getKey() + " --> " +
								"http://" + host + "/digestResults?id=" + 
								entry.getValue() + "\n\n";
		}
		
		return formattedString;
	}	
}
