package org.bd2k.crawler.crawler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.bd2k.crawler.service.PageService;
import org.bd2k.crawler.service.PageServiceImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

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
	
	//some borrowed from https://github.com/UCLA-BD2K/BD2K-Digester
	private final static int NUM_CRAWLERS = 1;
    private final static String USER_AGENT_NAME = "UCLA BD2K";
    private final static String CRAWLER_STORAGE = "temp/crawl/storage";
    private final static String LINCS_ID = "LINCS-DCIC";
    private final static String LINCS_URL = "http://lincs-dcic.org/";
    //private final static String LINCS_ID = "TestCenter";
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
    //represents JS files that were already visited
    private static Set<String> visitedJS = new HashSet<String>();
    
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
             
             System.out.println("Crawling centerid: " + getCenterID());
             System.out.println("results of crawl:");
             System.out.println(cleanText(text));
             System.out.println("doc id " + page.getWebURL().getDocid() );
             System.out.println(df.format(new Date()));
             
             //aggregate all needed information for DB storage
             String lastCrawlTime = df.format(new Date());
             String lastDiff = "";
             String currentContent = cleanText(text);

             org.bd2k.crawler.model.Page p = pageService.getPageByURLandCenterId(url, BD2KCrawler.centerID);         
            
             //if there is already an entry in the DB for this url+center combo,
             //need to compute a new diff (here, HTML string representation)
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
            	 
            	 //set the updates
            	 p.setLastCrawlTime(lastCrawlTime);
            	 p.setLastDiff(lastDiff);
            	 p.setCurrentContent(currentContent);
             }
             else {
            	 
            	 //else create a new document to add to DB
            	 p = new org.bd2k.crawler.model.Page(
            			 lastCrawlTime, 
            			 lastDiff, 
            			 currentContent,
            			 url,
            			 BD2KCrawler.centerID);
             }
             
             //store/update entry depending on logic above
             pageService.savePage(p);
             
             //if LINCS-DCIC (or other site with JS generated content)
             System.out.println("better be LINCS-DCIC: " + this.getCenterID());
             if(this.getCenterID().equals(LINCS_ID)) {
            	 System.out.println("in JS section");
            	 //get DOM representation
            	 Document doc = Jsoup.parseBodyFragment(htmlParseData.getHtml());
            	 Elements scripts = doc.getElementsByTag("script");
            	 
            	 //iterate through all scripts
            	 for(Element ele: scripts) {
            		 String src = ele.attr("src");
            		 System.out.println(src);
            		 
            		 //if the JS file has not been seen before this run and is valid, store to db
            		 if (src != null && !src.isEmpty() && src.startsWith("js/data")
            				 && !visitedJS.contains(src)) {
            			 
            			 visitedJS.add(src);
            			 System.out.println(src);
            			 GetRequest req = Unirest.get(LINCS_URL + src);
            			 
            			 //no need to parse into objects, we will store as is = string
            			 lastCrawlTime = df.format(new Date());
            			 p = pageService.getPageByURLandCenterId(LINCS_URL+src, this.getCenterID());
            			         			 
            			 try {
            				 System.out.println(req.asString().getBody());
            				 
            				 //reuse variables
            				 currentContent = req.asString().getBody();
            				 
            				 //if there is an existing entry in DB, check for new diff
            				 if(p != null) {
            					 if(!currentContent.equals(p.getCurrentContent())) {
            						 Digester d = new Digester(p.getCurrentContent(), currentContent);
            						 lastDiff = d.computeHTMLDiff();
            					 }
            					 else {
            						 lastDiff = p.getLastDiff();
            					 }
            					 
            					 //update values
            					 p.setLastCrawlTime(lastCrawlTime);
            	            	 p.setLastDiff(lastDiff);
            	            	 p.setCurrentContent(currentContent);
            				 }
            				 else {	//else add a new document
            					 lastDiff = "";	
            					 p = new org.bd2k.crawler.model.Page(
            							 lastCrawlTime,
            							 lastDiff,
            							 currentContent,
            							 LINCS_URL+src,
            							 this.getCenterID());
            				 }
            				 
            				 //save/update
            				 pageService.savePage(p);
            				 
            			 }
            			 catch(UnirestException e) {
            				 System.out.println("Error in req.asString()");
            				 e.printStackTrace();
            			 }
            			 catch(Exception e) {
            				 e.printStackTrace();
            			 }
            			 
            			 //left off here TODO
            			 //if a change, save to db like above
            		 }
            	 }
             }
             
             //if boolean flag set, there was a change and email needs
             //to be sent, or some equivalent
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
    	
    	//do a quick check to see if crawler is already running -- added safety, not required
    	if(controller != null) {
   
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
    	for(int i = 0; i < seedURLs.length; i++) {
    		controller.addSeed(seedURLs[i]);
    	}
    	
    	//start the crawler, blocks here until completion
    	controller.start(BD2KCrawler.class, NUM_CRAWLERS);
    	
    	//only gets here when no more crawling threads working
    	System.out.println("[BD2KCrawler] Finished crawling!");
    	resetCrawler();		//reset controller, officially marks crawling over
    	
    	return "Crawl complete.";
    }
    
    //allow outsiders to suggest that the crawler should stop.
    //in the future, may add more logic that denies stopping.
    public static boolean stopCrawling() {
    	
    	//if there is no crawler, then "stopping" technically succeeds
    	if(controller == null)
    		return true;
    	
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
    	
    	if(visitedJS != null) {
    		visitedJS.clear();
    	}
    }
}
