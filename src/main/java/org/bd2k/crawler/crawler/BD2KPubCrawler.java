package org.bd2k.crawler.crawler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bd2k.crawler.model.Publication;
import org.bd2k.crawler.service.PublicationService;
import org.bd2k.crawler.service.PublicationServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

/**
 * Customized implementation of our PubMed crawler by
 * bleakley. See: https://github.com/UCLA-BD2K/BD2KCCC-Pubmed.
 * This implementation diverges from the first mainly in how/what
 * results from crawls are stored.
 * 
 * Is a singleton class by simulating a static top level class (no instantiation).
 * For the most part, default values are what you want, but an init() family is
 * made avaialble for customization.
 * 
 * @author allengong
 *
 */
public class BD2KPubCrawler {
	
	// default members
	public static String[] bd2kGrants = { "GM114833", "EB020406", "HG007990", "HG008540", "AI117925", "AI117924", "EB020403", "GM114838", "HL127624", "HL127366", "EB020404", "EB020405", "HG007963"};
    public static String[] bd2kCenters = { "HeartBD2K", "BDDS", "BDTG", "CCD", "CEDAR", "CPCP", "ENIGMA", "KnowEnG", "LINCS-DCIC", "LINCS-TG", "MD2K", "Mobilize", "PIC-SURE" };
	public static String[] centersToCrawl = bd2kCenters;
	
	// private members for internal use
	private static boolean running = false;		// indicate if crawler is running
    
	// no instantiation possible
	private BD2KPubCrawler() {}
	
	// construction is through an init function
	public static void initPubCrawler(String[] grantArr, String[] centerArr) {
		
		bd2kGrants = grantArr;
		bd2kCenters = centerArr;
	}
	
	// overload init
	public static void initPubCrawler(String[] grantArr, String[] centerArr, 
			String[] toCrawl) {
		
		bd2kGrants = grantArr;
		bd2kCenters = centerArr;
		centersToCrawl = toCrawl;
	}

	/*
	 * Starts the crawler with configuration determined by init() or defaults.
	 * By default, if no centers to crawl specified, all centers will be crawled.
	 */
	public static Map<String, String> crawl() throws Exception {
		
		//safety check, to ensure only one crawl is every running at a time
		if(running) {
			return null;
		}
		
		// mark crawl as active
		running = true;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		String lastCrawlTime = df.format(new Date());
		
		// preprocess input
		Map<String, String> centerGrantMap = getCenterGrantMap();

		// begin		
		HashMap<Integer, ArrayList<String>> publications = 
				new HashMap<Integer, ArrayList<String>>();
		
		//loop through all of the centers to crawl
		for(int i = 0; i < centersToCrawl.length; i++) {
			String searchUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=" + 
					centerGrantMap.get(centersToCrawl[i]) + "[Grant%20Number]&retmode=json&retmax=1000";
			
			// make a request, parse response
			HttpResponse<JsonNode> req = Unirest.get(searchUrl).asJson();
			JSONObject json = req.getBody().getObject();
			JSONArray idList = json.getJSONObject("esearchresult").getJSONArray("idlist");
			
			// populate a map of publications and the centers that contribute
			for(int j = 0; j < idList.length(); j++) {
				
				int pmid = idList.optInt(j);
				if(!publications.containsKey(pmid)) {
					ArrayList<String> centers = new ArrayList<String>();
					centers.add(centersToCrawl[i]);
					publications.put(pmid, centers);
				}
				else {
					publications.get(pmid).add(centersToCrawl[i]);
				}
			}
		}	// end querying for publications
		
		// now we have all the publications, query for a summary of all
		Integer[] pmids = publications.keySet().toArray(new Integer[0]);
		System.out.println("num publications: " + pmids.length);
		String summaryURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&retmode=json&rettype=abstract&id=" + 
							pmids[0];
		
		// add the remaining elements with a comma 
		for(int i = 1; i < pmids.length; i++) {
			summaryURL += ("," + pmids[i]);
		}
		
		// make the request
		HttpResponse<JsonNode> req = Unirest.get(summaryURL).asJson();
		JSONObject json = req.getBody().getObject().getJSONObject("result");
		
		// important things we will use to keep track of changes + query db
		Map<String, String> changes = new HashMap<String, String>();
		PublicationService publicationService = new PublicationServiceImpl();
		
		// iterate through all pmids and check for "diff" with 
		// stored values (if a new publication then it also counts as a change)
		for(int i = 0; i < pmids.length; i++) {
			JSONObject pubJson = json.getJSONObject("" + pmids[i]);
			
			Publication p = new Publication();
			
			// title
			String unescapedPubTitle = pubJson.optString("title", "NO_TITLE");
			unescapedPubTitle = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(unescapedPubTitle);
			p.setTitle(unescapedPubTitle);
			
			// pmid and centers associated with this publication
			p.setPmid(pmids[i] + "");
			p.setCenters(publications.get(pmids[i]).toArray(new String[0]));
			
			// date
			String pubDate = pubJson.optString("sortpubdate", "1066/01/01 00:00").substring(0, 10);
			pubDate = pubDate.replace("/", "-");
			p.setPubDate(pubDate);
			
			// authors
			JSONArray authorsJson = pubJson.getJSONArray("authors");
			ArrayList<String> authorsList = new ArrayList<String>();
			for(int j = 0; j < authorsJson.length(); j++) {
				JSONObject jo = authorsJson.optJSONObject(j);
				String type = (String)jo.optString("authtype");
				
				// if there is an author field, add 
				if(jo.has("authtype") && type.equals("Author")) {
					authorsList.add((String)jo.optString("name"));
				}
			}
			
			p.setAuthors(authorsList.toArray(new String[0]));
			
			// journal + lastCrawlTime
			p.setJournal(pubJson.optString("fulljournalname"));
			p.setLastCrawlTime(lastCrawlTime);
			
			// check for diff and store/update to db
			// right now a diff = new publication
			Publication toStore = publicationService.getPublicationByPmid(p.getPmid());
			
			// if there is an existing document with the same pmid, update values
			if(toStore != null) {
				toStore.setAuthors(p.getAuthors());
				toStore.setCenters(p.getCenters());
				toStore.setJournal(p.getJournal());
				toStore.setLastCrawlTime(p.getLastCrawlTime());
				toStore.setPubDate(p.getPubDate());
				toStore.setTitle(p.getTitle());
			
				publicationService.savePublication(p);
			}
			else {
				
				// a new publication!! note differs from BD2KCrawler, diffs
				// only occur if the doc exists, so we don't know the _id here
				changes.put(centerGrantMap.get(p.getTitle()), p.getPmid());
				publicationService.savePublication(p);
			}
			
		}
		
		return changes;	//{ Title:pmid, ... }
	}
	
	
	/* private helpers */
	
	// using set members, generate a 1-1 map of center ids to their grant #s
	private static Map<String, String> getCenterGrantMap() {
		
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < bd2kCenters.length; i++) {
			map.put(bd2kCenters[i], bd2kGrants[i]);
		}
		
		return map;
	}
		
	/* getters and setters */
	
	public static String[] getBd2kGrants() {
		return bd2kGrants;
	}

	public static void setBd2kGrants(String[] bd2kGrants) {
		BD2KPubCrawler.bd2kGrants = bd2kGrants;
	}

	public static String[] getBd2kCenters() {
		return bd2kCenters;
	}

	public static void setBd2kCenters(String[] bd2kCenters) {
		BD2KPubCrawler.bd2kCenters = bd2kCenters;
	}

	public static String[] getCentersToCrawl() {
		return centersToCrawl;
	}

	public static void setCentersToCrawl(String[] centersToCrawl) {
		BD2KPubCrawler.centersToCrawl = centersToCrawl;
	}
	
	// checks to see if a crawl is ongoing
	public static boolean getCrawlerStatus() {
		
		return running;
	}
	

}
