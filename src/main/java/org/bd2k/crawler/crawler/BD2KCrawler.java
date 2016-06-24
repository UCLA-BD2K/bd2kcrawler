package org.bd2k.crawler.crawler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Customized implementation of crawler4j's WebCrawler.
 * Main entry point to start crawling.
 * 
 * For each site/file crawled, will update and/or store result 
 * including a diff of the previous result, if applicable.
 * 
 * @author allengong
 *
 */
public class BD2KCrawler extends WebCrawler {
	
	//Important members, some borrowed from https://github.com/UCLA-BD2K/BD2K-Digester
	private final static int NUM_CRAWLERS = 1;
    private final static String USER_AGENT_NAME = "UCLA BD2K";
    private final static String CRAWLER_STORAGE = "temp/crawl/storage";
    private final static String LINCS_ID = "LINCS-DCIC";
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");
    
    private String crawlID;	
    private String domain;		//similar to rootURL from previous crawler
    private String[] seedURLs;
    private String[] excludedURLs;
    
    //for generating timestamps
    private DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    
    public BD2KCrawler() {}	//should be avoided, or used in unison for initDefault()
 
    public BD2KCrawler(String crawlID, String domain, String[] seedURLs, String[] excludedURLs) {
		super();
		this.crawlID = crawlID;
		this.domain = domain;
		this.seedURLs = seedURLs;
		this.excludedURLs = excludedURLs;
	}
    
    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
    	 System.out.println("Coming from page: " + referringPage.getWebURL());
    	 System.out.println("deciding to visit page: " + url);
         String href = url.getURL().toLowerCase();	
         
         //skip filter file types by default (e.g. css, js, zip, etc.)
         if(FILTERS.matcher(href).matches())
        	 return false;
         System.out.println("Starts with domain: " + domain);
         //only check for files in the domain (e.g. https://bd2kccc.org)
         if(!href.startsWith(this.domain))
        	 return false;
         
         //skip specified urls to exclude
         for(int i = 0; i < excludedURLs.length; i++) {
        	 if(href.startsWith(excludedURLs[i]))
        		 return false;
         }
         
         return true;	//page is OK to crawl
     }
    
     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         System.out.println("Parsing URL: " + url);

         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
             String html = htmlParseData.getHtml();
             Set<WebURL> links = htmlParseData.getOutgoingUrls();

             System.out.println("Text length: " + text.length());
             System.out.println("Html length: " + html.length());
             System.out.println("Number of outgoing links: " + links.size());
         }
    }
    

    public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	/* setters and getters */
	public String getCrawlID() {
		return crawlID;
	}

	public void setCrawlID(String crawlID) {
		this.crawlID = crawlID;
	}

	public String[] getSeedURLs() {
		return seedURLs;
	}

	public void setSeedURLs(String[] seedURLs) {
		this.seedURLs = seedURLs;
	}

	public String[] getExcludedURLs() {
		return excludedURLs;
	}

	public void setExcludedURLs(String[] excludedURLs) {
		this.excludedURLs = excludedURLs;
	}
	
	
	/* Crawl handler, exposed as a public method */
    public void crawl() throws Exception {
    	
    	// Required for HTTPS sites; see http://stackoverflow.com/a/14884941
    	//SSL handshake fix
        System.setProperty("jsse.enableSNIExtension", "false");
    	
    	//configuration for crawler
    	CrawlConfig config = new CrawlConfig();
    	config.setCrawlStorageFolder(CRAWLER_STORAGE);
    	config.setPolitenessDelay(1000);				//1 req per sec, be nice!
    	
    	
    	//controller for this crawler
    	PageFetcher pf = new PageFetcher(config);
    	RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    	RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pf);
    	CrawlController controller = new CrawlController(config, pf, robotstxtServer);
    	
    	//add the seeds to indicate where crawler should start
    	for(int i = 0; i < seedURLs.length; i++)
    		controller.addSeed(seedURLs[i]);
    	
    	//start the crawler, blocks here until completion
    	controller.start(BD2KCrawler.class, NUM_CRAWLERS);
    	
    	System.out.println("[BD2KCrawler] Finished crawling!");
    }
}
