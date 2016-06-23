package org.bd2k.crawler.controller;

import org.bd2k.crawler.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller that handles requests for site pages.
 * Not to be confused with CrawlerAPIController, which exposes API endpoints.
 * @author allengong
 *
 */
@Controller
public class CrawlerSiteController {
	@Autowired
	private PageService archiveService;

	/* for testing functionality */
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public String getTest() {
		
		System.out.println(archiveService.ping());
		
		return "test";
	}
	
	/* homepage */
	@RequestMapping(value="/", method=RequestMethod.GET) 
	public String getHomePage() {
		return "index";
	}
}

