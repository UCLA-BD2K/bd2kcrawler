package org.bd2k.crawler.crawler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bd2k.crawler.model.Publication;
import org.bd2k.crawler.model.PublicationResult;
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
	public static final String[] bd2kGrants = { "GM114833", "EB020406", "HG007990", "HG008540", "AI117925", "AI117924", "EB020403", "GM114838", "HL127624", "HL127366", "EB020404", "EB020405", "HG007963"};
    public static final String[] bd2kCenters = { "HeartBD2K", "BDDS", "BDTG", "CCD", "CEDAR", "CPCP", "ENIGMA", "KnowEnG", "LINCS-DCIC", "LINCS-TG", "MD2K", "Mobilize", "PIC-SURE" };
	public static String[] centersToCrawl = bd2kCenters;
	
	// private members for internal use
	private static boolean running = false;		// indicate if crawler is running
    private static Map<String, String> changes;	// any changes found during crawl
	
	// instantiation should be used only for checks (i.e. crawling status without blocking)
	public BD2KPubCrawler() {}
	
	/*
	 * Starts the crawler with current configuration.
	 * By default, if no centers to crawl specified, all centers will be crawled.
	 */
	public static Map<String, String> crawl() throws Exception {
		
		//safety check, to ensure only one crawl is every running at a time
		if(running) {
			return null;
		}
		
		// mark crawl as active
		running = true;
		changes = new HashMap<String, String>();	// private static
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		// preprocess input
		Map<String, String> centerGrantMap = getCenterGrantMap();
				
		//loop through all of the centers, crawl one by one
		for(int i = 0; i < centersToCrawl.length; i++) {
			String searchUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term=" + 
					centerGrantMap.get(centersToCrawl[i]) + "[Grant%20Number]&retmode=json&retmax=1000";
			
			// make a request, parse response
			HttpResponse<JsonNode> req = Unirest.get(searchUrl).asJson();
			JSONObject json = req.getBody().getObject();
			JSONArray idList = json.getJSONObject("esearchresult").getJSONArray("idlist");
						
			// now we have all the publications for this center, query for a summary of pmids
			if(idList.length() == 0) {
				
				// move onto next center
				continue;
			}
			
			String summaryURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&retmode=json&rettype=abstract&id=" + 
								idList.optInt(0);
			
			// add the remaining elements with a comma 
			for(int j = 1; j < idList.length(); j++) {
				summaryURL += ("," + idList.optInt(j));
			}
			
			// make the request (reuse variables)
			req = Unirest.get(summaryURL).asJson();
			json = req.getBody().getObject().getJSONObject("result");
			
			String lastCrawlTime = df.format(new Date());
			
			// important things we will use to keep track of changes + query db
			List<Publication> publicationsList = new ArrayList<Publication>();
			PublicationService publicationService = new PublicationServiceImpl();
			
			// iterate through all pmids for this center and check for differences
			// (a new publication counts as a change)
			for(int k = 0; k < idList.length(); k++) {
				
				JSONObject pubJson = json.getJSONObject("" + idList.optInt(k));
				
				Publication p = new Publication();
				
				// title
				String unescapedPubTitle = pubJson.optString("title", "NO_TITLE");
				unescapedPubTitle = org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(unescapedPubTitle);
				p.setTitle(unescapedPubTitle);
				
				// pmid and centers associated with this publication
				p.setPmid(idList.optInt(k) + "");
				//p.setCenters(publications.get(pmids[i]).toArray(new String[0]));
				
				// date
				String pubDate = pubJson.optString("sortpubdate", "1066/01/01 00:00").substring(0, 10);
				pubDate = pubDate.replace("/", "-");
				p.setPubDate(pubDate);
				
				// authors
				JSONArray authorsJson = pubJson.getJSONArray("authors");
				ArrayList<String> authorsList = new ArrayList<String>();
				for(int m = 0; m < authorsJson.length(); m++) {
					JSONObject jo = authorsJson.optJSONObject(m);
					String type = (String)jo.optString("authtype");
					
					// if there is an author field, add 
					if(jo.has("authtype") && type.equals("Author")) {
						authorsList.add((String)jo.optString("name"));
					}
				}
				
				p.setAuthors(authorsList.toArray(new String[authorsList.size()]));
				
				// journal + lastCrawlTime
				p.setJournal(pubJson.optString("fulljournalname"));
				//p.setLastCrawlTime(lastCrawlTime);
				
				// add Publication record to rolling list for this center
				publicationsList.add(p);
				
			}

			// generate PublicationResult entity to store in DB
			PublicationResult pResult = new PublicationResult();
			pResult.setCenterID(centersToCrawl[i]);
			
			// prepare content+full content
			String[] pmids = new String[idList.length()];
			for(int n = 0; n < idList.length(); n++) {
				pmids[n] = "" + idList.optInt(n);
			}
			pResult.setCurrentContent(pmids);
			pResult.setFullContent(publicationsList
					.toArray(new Publication[publicationsList.size()]));
			pResult.setLastCrawlTime(lastCrawlTime);
			pResult.setLastDiff(new String[0]);		//may change, see below
			
			// check to see if there is already an existing document
			PublicationResult check = 
					publicationService.getPublicationResultByCenterID(centersToCrawl[i]);
			
			// if there is a doc, need to check for diff + changes
			if(check != null) {
				
				Set<String> previousContent = new HashSet<String>(Arrays.asList(check.getCurrentContent()));
				String[] copyOfPmids = Arrays.copyOf(pmids, pmids.length);
				Set<String> currentContent = new HashSet<String>(Arrays.asList(copyOfPmids));	// use copy because removeAll() changes underlying array
				currentContent.removeAll(previousContent);	// emulate set difference
				
				// if there are changes found in this crawl, make a note + store
				if(currentContent.size() > 0) {
					
					String newPmids = "";					// comma separated representation
					for(String s : currentContent) {
						newPmids += (s + ", ");
					}
					changes.put(centersToCrawl[i], newPmids);
					
					// set the diff
					pResult.setLastDiff(currentContent.toArray(new String[currentContent.size()]));
				}
				
				//publicationService.savePublicationResult(pResult);
				
			}
			else {	// else everything found was new
				
				String newPmids = "";
				for(int j = 0; j < pResult.getCurrentContent().length; j++) {
					newPmids += (pResult.getCurrentContent()[j] + ", ");
				}
				
				changes.put(centersToCrawl[i], newPmids);
				
				//publicationService.savePublicationResult(pResult);
			}
			
			
		}	// end querying for publications -- really end all main logic
		
		
		// by storing a new reference and resetting crawler, ensure no conflicts
		Map<String, String> temp = changes;	// store results since next line will clear
		resetCrawler();
		
		return temp;	//{ Title:pmid, ... }
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
	
	// reset crawler to pristine state
	private static void resetCrawler() {
		centersToCrawl = bd2kCenters;
		changes = new HashMap<String, String>();
		running = false;
	}
		
	/* getters and setters */
	
	public static String[] getBd2kGrants() {
		return bd2kGrants;
	}

	public static String[] getBd2kCenters() {
		return bd2kCenters;
	}

	public static String[] getCentersToCrawl() {
		return centersToCrawl;
	}

	public static void setCentersToCrawl(String[] centersToCrawl) {
		BD2KPubCrawler.centersToCrawl = centersToCrawl;
	}
	
	// checks to see if a crawl is ongoing
	public static int getCrawlerStatus() {
		
		if(running) {
			return 1;
		}
		
		return 0;	// idle.
	}
}
