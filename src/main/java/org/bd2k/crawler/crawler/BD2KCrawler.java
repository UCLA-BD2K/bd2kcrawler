package org.bd2k.crawler.crawler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import org.bd2k.crawler.service.PageService;
import org.bd2k.crawler.service.PageServiceImpl;

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
	
	//cannot autowire because there will be instances to this class
	private PageService pageService = new PageServiceImpl();
	
	//Important members, some borrowed from https://github.com/UCLA-BD2K/BD2K-Digester
	private final static int NUM_CRAWLERS = 1;
    private final static String USER_AGENT_NAME = "UCLA BD2K";
    private final static String CRAWLER_STORAGE = "temp/crawl/storage";
    private final static String LINCS_ID = "LINCS-DCIC";
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");
    
    //members
    private static String centerID;	
    private static String domain;		//similar to rootURL from previous crawler
    private static String[] seedURLs;
    private static String[] excludedURLs;
    
    //controller to expose control over the crawler from outside
    private static CrawlController controller = null;
    
    //for use on sites that hold content in javascript, rather than html (LINCS-DCIC)
    private static Set<String> visitedJS;
    
    //for generating timestamps
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    
    public BD2KCrawler() {}	//should be avoided, or used in unison for initDefault()
 
    public BD2KCrawler(String centerID, String domain, String[] seedURLs, String[] excludedURLs) {
		super();
		BD2KCrawler.centerID = centerID;
		BD2KCrawler.domain = domain;
		BD2KCrawler.seedURLs = seedURLs;
		BD2KCrawler.excludedURLs = excludedURLs;
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
    	 
    	 System.out.println("deciding whether to visit page: " + url);
    	 
         String href = url.getURL().toLowerCase();	
         
         //skip filter file types by default (e.g. css, js, zip, etc.)
         if(FILTERS.matcher(href).matches())
        	 return false;
         
         //only check for files in the domain (e.g. https://bd2kccc.org)
         if(!href.startsWith(BD2KCrawler.domain))
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
    	 System.out.println("[ Crawler ] Visiting url: " + url);
    	 
         if (page.getParseData() instanceof HtmlParseData) {
             HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
             String text = htmlParseData.getText();
             //String html = htmlParseData.getHtml();
             //Set<WebURL> links = htmlParseData.getOutgoingUrls();

             //System.out.println("Text length: " + text.length());
             //System.out.println("Html length: " + html.length());
             //System.out.println("Number of outgoing links: " + links.size());
             
             System.out.println("Crawling centerid: " + getCenterID());
             System.out.println("results of crawl:");
             System.out.println(cleanText(text));
             System.out.println("doc id " + page.getWebURL().getDocid() );
             System.out.println(df.format(new Date()));
             
             //aggregate all needed information for DB storage
             String lastCrawlTime = df.format(new Date());
             String lastDiff = "";
             //String previousCrawlContent;	//omitted, not really important
             String currentContent = cleanText(text);

             org.bd2k.crawler.model.Page p = pageService.getPageByURLandCenterId(url, BD2KCrawler.centerID);         
            
             //if there is already an entry in the DB for this url+center combo,
             //need to compute a new diff first (here, HTML string representation)
             if(p != null) {
            	 
            	 //check if there is no change, skip generating diff
            	 if(!currentContent.equals(p.getCurrentContent())) {
            		 Digester d = new Digester(p.getCurrentContent(), currentContent);
                	 lastDiff = d.computeHTMLDiff(); 
            	 }
            	 else {
            		 System.out.println("skip digester");
            		 lastDiff = p.getLastDiff();	//same diff as before
            	 }
            	 
            	 //store the updates
            	 p.setLastCrawlTime(lastCrawlTime);
            	 p.setLastDiff(lastDiff);
            	 p.setCurrentContent(currentContent);
            	 
            	 pageService.savePage(p);
             }
             else {
            	 
            	 System.out.println("woooooo");
            	 p = new org.bd2k.crawler.model.Page(
            			 lastCrawlTime, 
            			 lastDiff, 
            			 currentContent,
            			 url,
            			 BD2KCrawler.centerID);
            	 
            	 pageService.savePage(p);
             }       
         }
    }
    
    /* setters and getters */
     
    public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		BD2KCrawler.domain = domain;
	}

	public String getCenterID() {
		return centerID;
	}

	public void setCrawlID(String centerID) {
		BD2KCrawler.centerID = centerID;
	}

	public String[] getSeedURLs() {
		return seedURLs;
	}

	public void setSeedURLs(String[] seedURLs) {
		BD2KCrawler.seedURLs = seedURLs;
	}

	public String[] getExcludedURLs() {
		return excludedURLs;
	}

	public void setExcludedURLs(String[] excludedURLs) {
		BD2KCrawler.excludedURLs = excludedURLs;
	}
	
	
	/* Crawl handler, exposed as a public method */
    public static String crawl() throws Exception {
    	
    	//do a quick check to see if crawler is already running
    	if(controller != null) {
    		//return "Crawling in progress"
    		return "Crawling already in progress...";
    	}
    	
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
    	controller = new CrawlController(config, pf, robotstxtServer);
    	
    	//add the seeds to indicate where crawler should start
    	for(int i = 0; i < seedURLs.length; i++)
    		controller.addSeed(seedURLs[i]);
    	
    	//start the crawler, blocks here until completion
    	controller.start(BD2KCrawler.class, NUM_CRAWLERS);
    	
    	//only gets here when no more crawling threads working
    	System.out.println("[BD2KCrawler] Finished crawling!");
    	resetCrawler();	//reset controller, officially marks crawling over
    	return "Crawl complete.";
    }
    
    //allow outsiders to suggest that the crawler should stop - not tested yet
    public boolean stopCrawling() {
    	
    	if(controller == null)
    		return false;
    	
    	//can implement this in any way, for simplicity we will just stop
    	controller.shutdown();
    	controller.waitUntilFinish();
    	
    	return true;
    }
    
    // get status of crawler
    // 1=running, 0=idle
    public int getCrawlerStatus() {
    	
    	if(controller != null) {
    		return 1;	//crawler is running
    	}
    	
    	return 0;	//crawler is idle
    }
    
    // collapse extra whitespace, borrowed from https://github.com/UCLA-BD2K/BD2K-Digester
    private String cleanText(String text) {
        text = text.replaceAll("[ \\t]+", " "); // Collapse whitespace
        text = text.replaceAll("[ \\t]*\\n+[ \\t]*", "\n"); // Trim whitespace
        text = text.replaceAll("\\n+", "\n"); // Collapse empty lines
        return text;
    }
    
    // private function to reset crawler to a clean state
    private static void resetCrawler() {
    	controller = null;
    	
    }
}
