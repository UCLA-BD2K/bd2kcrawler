package org.bd2k.crawler.controller;

import org.bd2k.crawler.crawler.BD2KCrawler;
import org.bd2k.crawler.dao.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for making API requests for the Crawler web app.
 * @author allengong
 *
 */
@RestController
public class CrawlerAPIController {
	
	private BD2KCrawler crawler;	//singleton instance used to call crawl ops
	
	/*
	 * Endpoint for checking a single website for changes.
	 */
	@RequestMapping(value="/news/update/{id}")
	public String getUpdateForId(@PathVariable String id) {
		
		//id = URL?
		System.out.println("woo" + id);
		
		return "test";
	}
	
	@RequestMapping(value="/testInsert")
	public Page insertIntoDB(@RequestParam("url")String url) {
		
		return new Page();
	}
	
}
