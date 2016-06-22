package org.bd2k.crawler.crawler;

import edu.uci.ics.crawler4j.crawler.WebCrawler;

/**
 * Customized implementation of crawler4j's WebCrawler.
 * Main entry point to start crawling.
 * 
 * @author allengong
 *
 */
public class BD2KCrawler extends WebCrawler {
	
	//Important macros
	private final static int NUM_CRAWLERS = 1;
    private final static String USER_AGENT_NAME = "UCLA BD2K";
    private final static String CRAWLER_STORAGE = "temp/crawler/storage";
    private final static String LINCS_ID = "LINCS-DCIC";
    
    public BD2KCrawler() {
    	
    }
}
