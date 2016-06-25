package org.bd2k.crawler.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.bd2k.crawler.crawler.BD2KCrawler;
import org.bd2k.crawler.crawler.Digester;
import org.bd2k.crawler.model.Center;
import org.bd2k.crawler.service.CenterService;
import org.bd2k.crawler.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for /news, may initiate crawling.
 * @author allengong
 *
 */
@RestController
public class NewsController {
	
	@Autowired
	private PageService pageServce;
	
	@Autowired
	private CenterService centerService;
	
	//class crawler and digester
	private static BD2KCrawler crawler;
	
	/**
	 * domain that the crawler will crawl
	 */
	@Value("${crawler.domain}")
	String domain;
	
	/*
	 * Checks all websites and checks if there are any changes since the last check. 
	 * If the process is already running, it should return the status.*/
	@RequestMapping(value="/news/update", method=RequestMethod.GET)
	public void getNewCrawlData(HttpServletResponse res) {
		
		System.out.println("running crawler...");
		
		//redirects to result
		try {
			//
			res.sendRedirect("/BD2KCrawler/test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Only checks the website associated with {id}.
	 */
	@RequestMapping(value="/news/update/{id}", method=RequestMethod.GET) 
	public void getNewCrawlDataForId(HttpServletResponse res, 
			@PathVariable("id") String id) {
		
		System.out.println("running crawler for centerID: " + id);
		
		List<Center> centers = centerService.getAllCenters();
		String seedURL = null;
		for(Center c: centers) {
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
				crawler = new BD2KCrawler(id, domain, seeds, excludes);
				
				//attempt to crawl
				try {
					crawler.crawl();
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
		
		
		//redirect to result page
		try {
			res.sendRedirect("/BD2KCrawler/test");
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Allows the viewers to see what the diffs are from all websites.
	 */
	@RequestMapping(value="/news/changes", method=RequestMethod.GET) 
	public void getNewChanges(HttpServletResponse res) {
		System.out.println("grabbing diffs for all websites.");
		
		//redirect to result page
		try {
			res.sendRedirect("/BD2KCrawler/test");
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Allows the viewers to see what the diffs are from the website associated with [id].
	 */
	@RequestMapping(value="/news/changes/{id}", method=RequestMethod.GET) 
	public void getNewChanges(HttpServletResponse res, 
			@PathVariable("id") String id) {
		System.out.println("grabbing diff for just: " + id);
		
		//redirect to result page
		try {
			res.sendRedirect("/BD2KCrawler/test");
		}
		catch(Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
