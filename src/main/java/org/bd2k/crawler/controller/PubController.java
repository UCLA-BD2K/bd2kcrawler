package org.bd2k.crawler.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.bd2k.crawler.crawler.BD2KPubCrawler;
import org.bd2k.crawler.crawler.Email;
import org.bd2k.crawler.model.PublicationResult;
import org.bd2k.crawler.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for /pub, may initiate crawling.
 * @author allengong
 *
 */
@RestController
public class PubController {
	
	@Autowired 
	PublicationService publicationService;
	
	@Value("${email.recipients}")
	private String emailRecipients;		// comma separated emails (no spaces)
	
	private final int CRAWLER_RUNNING = 1;
	private final int CRAWLER_IDLE = 0;
	
	private DateFormat df;
	
	/**
	 * Checks all publications and checks if there are any changes since the last check. 
	 * If the process is already running, it should return the status.
	 */
	@RequestMapping(value="/pub/update", method=RequestMethod.GET)
	public String getNewPubData(HttpServletResponse res) {
		
		System.out.println("crawling all publications");
		df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String crawlStartTime = df.format(new Date());
		
		// sentinel object used for non-blocking check of status
		BD2KPubCrawler crawler = new BD2KPubCrawler();
		
		// results of the crawler, init to bypass uninitialized warning
		Map<String, String> results = new HashMap<String,String>();
		
		if (crawler.getCrawlerStatus() == CRAWLER_RUNNING) {
			
			return "[ ! ] Crawler is already running, please try again later";
		}
		
		try {
			 results = BD2KPubCrawler.crawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// send email with the changes
		List<String> recipients = new ArrayList<String>();
		
		String[] recipientArr = emailRecipients.split(",");
		for(int i = 0 ; i < recipientArr.length; i++) {
			recipients.add(recipientArr[i]);
		}
		
		String subject = "[BD2K PubCrawler] Results for crawl";
		String header = "Request /pub/update\n";
		header += "Crawl initiated on: " + crawlStartTime + "\n";
		
		String body = header;	// start with the header
		String attachment = "";
		body += "\n-------- RESULTS --------\n\n";
		body += formatCrawlResultsForEmail(results);
		
		sendCrawlResultsEmail(subject, body.substring(0, body.length()-1), attachment, recipients);
				
		return formatCrawlResultsForEmail(results);	//temp for viewing
	}
	
	/**
	 * Only checks the publications associated with [id].
	 */
	@RequestMapping(value="/pub/update/{id}", method=RequestMethod.GET)
	public String getNewPubDataForId(HttpServletResponse res,
			@PathVariable("id") String id) {
		
		System.out.println("crawling publications with center: " + id);
		df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String crawlStartTime = df.format(new Date());
		
		String[] centers = {id};
		BD2KPubCrawler.setCentersToCrawl(centers);
		
		// results of the crawler, init to bypass uninitialized warning
		Map<String, String> results = new HashMap<String,String>();
		
		// sentinel object used for non-blocking check of status
		BD2KPubCrawler crawler = new BD2KPubCrawler();
		
		if (crawler.getCrawlerStatus() == CRAWLER_RUNNING) {
			
			return "[ ! ] Crawler is already running, please try again later";
		}
		
		try {
			 results = BD2KPubCrawler.crawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// send email with the changes
		List<String> recipients = new ArrayList<String>();
		
		String[] recipientArr = emailRecipients.split(",");
		for(int i = 0 ; i < recipientArr.length; i++) {
			recipients.add(recipientArr[i]);
		}
		
		String subject = "[BD2K PubCrawler] Results for crawl";
		String header = "Request /pub/update/" + id + "\n";
		header += "Crawl initiated on: " + crawlStartTime + "\n";
		
		String body = header;	// start with the header
		String attachment = "";
		body += "\n-------- RESULTS for: " + id + " --------\n\n";
		body += formatCrawlResultsForEmail(results);
		
		sendCrawlResultsEmail(subject, body.substring(0, body.length()-1), attachment, recipients);
				
		return formatCrawlResultsForEmail(results);	//temp for viewing
	}
	
	/**
	 * Allows the viewers to see what the diffs are from all publications.
	 */
	@RequestMapping(value="/pub/changes", method=RequestMethod.GET)
	public List<PublicationResult> getPubChanges(HttpServletResponse res) {
		
		return publicationService.getAllPublicationResults();
	}
	
	/**
	 * Allows the viewers to see what the diffs are from the publications associated with [id].
	 */
	@RequestMapping(value="/pub/changes/{id}", method=RequestMethod.GET)
	public PublicationResult getPubChangesForId(HttpServletResponse res,
			@PathVariable("id") String id) {
		
		return publicationService.getPublicationResultByCenterID(id);
		
	}
	
	/**
	 * Returns the current status of the crawler in text representation.
	 */
	@RequestMapping(value="/pub/crawlerStatus")
	public String getCrawlerStatus() {
		
		BD2KPubCrawler crawler = new BD2KPubCrawler();
		
		// accessing it this way prevents any blocking due to class crawling
		if (crawler.getCrawlerStatus() == CRAWLER_RUNNING) {
			return "Crawler is currently running.";
		}
		
		return "Crawler is idle.";
		
	}
	
	/* private helpers */
	
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
		
		//formats specific to the pubmed crawl results.
		if (results.isEmpty()) {
			return "NO CHANGES.";
		}
		
		String ret = "";
		for (Map.Entry<String, String> entry : results.entrySet()) {
			ret += (
					"(" + entry.getKey() + ")" + " " + entry.getValue() + "\n\n"
					);
		}
		
		return ret;
	}
}
