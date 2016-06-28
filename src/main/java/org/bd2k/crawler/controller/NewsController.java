package org.bd2k.crawler.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.bd2k.crawler.crawler.BD2KCrawler;
import org.bd2k.crawler.model.Center;
import org.bd2k.crawler.model.Page;
import org.bd2k.crawler.service.CenterService;
import org.bd2k.crawler.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	//class crawler and digester
	private static BD2KCrawler crawler;
	
	private final int CRAWLER_RUNNING = 1;
	private final int CRAWLER_IDLE = 0;
		
	/*
	 * Checks all websites and checks if there are any changes since the last check. 
	 * If the process is already running, it should return the status.*/
	@RequestMapping(value="/news/update", method=RequestMethod.GET)
	public String getNewCrawlData(HttpServletResponse res) {
		
		System.out.println("running crawler on all sites...");
		
		List<Center> centers = centerService.getAllCenters();
		String[] seeds = new String[centers.size()];
		String[] excludes = {};
		
		for(int i = 0; i < seeds.length; i++) {
			seeds[i] = centers.get(i).getSiteURL();
		}
		
		//if no crawler instance
		if(crawler == null) {
			crawler = new BD2KCrawler();
		}
		
		if(crawler.getCrawlerStatus() == CRAWLER_IDLE) {
			
			//if crawler is idle, then iteratively crawl all centers
			for(int i = 0; i < seeds.length; i++) {
				
				String[] currSeed = {seeds[i]};			//for each site
				
				//current crawler info
				crawler = new BD2KCrawler(
						centers.get(i).getCenterID(), 
						seeds[i], 
						currSeed, 
						excludes);
				
				try {
					System.out.println("going to crawl: " + crawler.getCenterID());
					BD2KCrawler.crawl();	//blocks here until crawl complete
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		else {
			return "[ ! ]: Crawler already running, please try again later.";
		}
		
		return "[ OK ]: Crawl complete.";
	}
	
	/*
	 * Only checks the website associated with {id}.
	 */
	@RequestMapping(value="/news/update/{id}", method=RequestMethod.GET) 
	public String getNewCrawlDataForId(HttpServletResponse res, 
			@PathVariable("id") String id) {
		
		System.out.println("running crawler for centerID: " + id);
		
		List<Center> centers = centerService.getAllCenters();
		String seedURL = null;
		for(Center c : centers) {
			System.out.println(c.getCenterID());
			if(c.getCenterID().equals(id)) {
				seedURL = c.getSiteURL();
				break;
			}
		}
		
		//if a valid center id, proceed
		if(seedURL!=null) {
			String[] seeds = {seedURL};
			String[] excludes = {};
			
			//ideally if it is not null, we want to return to the user to let them 
			//know that a crawl is already taking place
			if(crawler == null) {
				crawler = new BD2KCrawler(id, seedURL, seeds, excludes);
				
				//Crawler is running!!!
				if(crawler.getCrawlerStatus() == CRAWLER_RUNNING) {
					return "[ ! ]: Crawler is already running, please try again later.";
				}
				
				//attempt to crawl
				try {
					BD2KCrawler.crawl();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Crawl already going on...");
			}
		}
		else {
			System.out.println("No matching id");
		}
		
		
		return "[ OK ]: Crawl complete.";
	}
	
	/*
	 * Allows the viewers to see what the diffs are from all websites.
	 */
	@RequestMapping(value="/news/changes", method=RequestMethod.GET) 
	public List<Page> getNewChanges(HttpServletResponse res,
			@RequestParam(value="limit", required=false) Integer limit,
			@RequestParam(value="offset", required=false) Integer offset) {
		
		if(limit != null && offset != null) {
			return pageService.getAllPagesLimOff(limit, offset);
		}
		
		return pageService.getAllPages();
	}
	
	/*
	 * Allows the viewers to see what the diffs are from the website associated with [id].
	 */
	@RequestMapping(value="/news/changes/{id}", method=RequestMethod.GET) 
	public List<Page> getNewChanges(HttpServletResponse res, 
			@PathVariable("id") String id,
			@RequestParam(value="limit", required=false) Integer limit,
			@RequestParam(value="offset", required=false) Integer offset) {
			
		if(limit != null && offset != null) {
			return pageService.getPagesByCenterIDLimOff(limit, offset, id);
		}
		
		return pageService.getPagesByCenterID(id);
	}
	
	/*
	 * Allows viewers to see the status of the crawler
	 */
	@RequestMapping(value="/news/crawlerStatus")
	public String getCrawlerStatus() {
		
		int status = new BD2KCrawler().getCrawlerStatus();
		
		if(status == CRAWLER_RUNNING) {
			return "Crawler is currently running...";
		}
		
		return "Crawler is idle.";
		
	}
	
	/*
	 * Allows moderators to request that the crawler be stopped.
	 */
	@RequestMapping(value="/news/crawlerStop")
	public String stopCrawler() {
		
		return "Crawler stopped: " + BD2KCrawler.stopCrawling();
	}
}
