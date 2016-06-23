package org.bd2k.crawler.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

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
	
	//@Autowired --- do we need a separate publications service?
	
	/*
	 * Checks all publications and checks if there are any changes since the last check. 
	 * If the process is already running, it should return the status.
	 */
	@RequestMapping(value="/pub/update", method=RequestMethod.GET)
	public void getNewPubData(HttpServletResponse res) {
		
		System.out.println("crawling all publications");
		
		try {
			res.sendRedirect("/BD2KCrawler/test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Only checks the publications associated with [id].
	 */
	@RequestMapping(value="/pub/update/{id}", method=RequestMethod.GET)
	public void getNewPubDataForId(HttpServletResponse res,
			@PathVariable("id") String id) {
		
		System.out.println("crawling all publications");
		
		try {
			res.sendRedirect("/BD2KCrawler/test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}
