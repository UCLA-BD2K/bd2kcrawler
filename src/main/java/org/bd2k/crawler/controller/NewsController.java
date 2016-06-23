package org.bd2k.crawler.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.bd2k.crawler.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
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
		System.out.println("running crawler for " + id);
		
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
