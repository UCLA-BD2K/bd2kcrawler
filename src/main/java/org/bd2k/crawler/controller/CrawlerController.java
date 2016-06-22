package org.bd2k.crawler.controller;

import org.bd2k.crawler.service.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller that handles GET requests outlined in Spec
 * @author allengong
 *
 */

@Controller
public class CrawlerController {
	@Autowired
	@Qualifier(value="archiveService")
	private ArchiveService archiveService;

	@RequestMapping(value="/test", method=RequestMethod.GET)
	public String getTest() {
		
		System.out.println(archiveService.ping());
		
		return "test";
	}
	
	/*
	 * Endpoint for checking a single website for changes.
	 * 
	 * GET /news/update/[id]
	 */
	@RequestMapping(value="/news/update/{id}")
	public String getUpdateForId(@PathVariable String id) {
		
		//id = URL?
		System.out.println(id);
		
		return "test";
	}
	
}
