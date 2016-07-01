package org.bd2k.crawler.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.bd2k.crawler.crawler.BD2KPubCrawler;
import org.bd2k.crawler.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for /pub, may initate crawling.
 * @author allengong
 *
 */
@RestController
public class PubController {
	
	@Autowired 
	PublicationService publicationService;
	
	private final int CRAWLER_RUNNING = 1;
	private final int CRAWLER_IDLE = 0;
	
	/*
	 * Checks all publications and checks if there are any changes since the last check. 
	 * If the process is already running, it should return the status.
	 */
	@RequestMapping(value="/pub/update", method=RequestMethod.GET)
	public String getNewPubData(HttpServletResponse res) {
		
		System.out.println("crawling all publications");
		
		// sentinel object used for non-blocking check of status
		BD2KPubCrawler crawler = new BD2KPubCrawler();
		
		// results of the crawler, init to bypass uninitialized warning
		Map<String, String> results = new HashMap<String,String>();
		
		if(crawler.getCrawlerStatus() == CRAWLER_RUNNING) {
			
			return "[ ! ] Crawler is already running, please try again later";
		}
		
		try {
			 results = BD2KPubCrawler.crawl();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
				
		return formatCrawlResultsForEmail(results);	//temp for viewing
	}
	
	/*
	 * Only checks the publications associated with [id].
	 */
	@RequestMapping(value="/pub/update/{id}", method=RequestMethod.GET)
	public String getNewPubDataForId(HttpServletResponse res,
			@PathVariable("id") String id) {
		
		System.out.println("crawling publications with center: " + id);
		
		String[] centers = {id};
		BD2KPubCrawler.setCentersToCrawl(centers);
		
		// results of the crawler, init to bypass uninitialized warning
		Map<String, String> results = new HashMap<String,String>();
		
		// sentinel object used for non-blocking check of status
		BD2KPubCrawler crawler = new BD2KPubCrawler();
		
		if(crawler.getCrawlerStatus() == CRAWLER_RUNNING) {
			
			return "[ ! ] Crawler is already running, please try again later";
		}
		
		try {
			 results = BD2KPubCrawler.crawl();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
				
		return formatCrawlResultsForEmail(results);	//temp for viewing
	}
	
	/*
	 * Allows the viewers to see what the diffs are from all publications.
	 */
	@RequestMapping(value="/pub/changes", method=RequestMethod.GET)
	public void getPubChanges(HttpServletResponse res) {
		
		System.out.println("grabbing diffs for all pubs");
		
		try {
			res.sendRedirect("/BD2KCrawler/test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Allows the viewers to see what the diffs are from the publications associated with [id].
	 */
	@RequestMapping(value="/pub/changes/{id}", method=RequestMethod.GET)
	public void getPubChangesForId(HttpServletResponse res,
			@PathVariable("id") String id) {
		
		System.out.println("grabbing diffs for just pub: " + id);
		
		try {
			res.sendRedirect("/BD2KCrawler/test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Returns the current status of the crawler in text representation.
	 */
	@RequestMapping(value="/pub/crawlerStatus")
	public String getCrawlerStatus() {
		
		BD2KPubCrawler crawler = new BD2KPubCrawler();
		
		// accessing it this way prevents any blocking due to class crawling
		if(crawler.getCrawlerStatus() == CRAWLER_RUNNING) {
			return "Crawler is currently running.";
		}
		
		return "Crawler is idle.";
		
	}
	
	/* private helpers */
	private String formatCrawlResultsForEmail(Map<String, String> results) {
		
		//formats specific to the pubmed crawl results.
		if(results.isEmpty()) {
			return "NO CHANGES.";
		}
		
		String ret = "";
		for(Map.Entry<String, String> entry : results.entrySet()) {
			ret += (
					"(" + entry.getKey() + ")" + " " + entry.getValue() + "<br/>"
					);
		}
		
		return ret;
	}
}
