package org.bd2k.crawler.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.bd2k.crawler.model.Center;
import org.bd2k.crawler.model.Page;
import org.bd2k.crawler.model.Publication;
import org.bd2k.crawler.model.PublicationResult;
import org.bd2k.crawler.service.CenterService;
import org.bd2k.crawler.service.PageService;
import org.bd2k.crawler.service.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller that handles requests for site pages.
 * Not to be confused with CrawlerAPIController, which exposes API endpoints.
 * @author allengong
 *
 */
@Controller
public class CrawlerSiteController {
	@Autowired
	private PageService pageService;
	
	@Autowired
	private CenterService centerService;
	
	@Autowired
	private PublicationService publicationService;


	/* for testing functionality */
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public String getTest() {
		
		System.out.println(pageService.ping());
		
		return "test";
	}
	
	/* homepage */
	@RequestMapping(value="/index", method=RequestMethod.GET) 
	public String getHomePage(Principal p, 
			@RequestParam(value="error", required=false) String error,
			@RequestParam(value="logout", required=false) String logout,
			Model model) {
		
		if (p != null) {
			System.out.println("principal name " + p.getName());
			return "redirect:dashboard";
		}
		
		if (error != null) {
			model.addAttribute("error", true);
		} else if(logout != null) {
			model.addAttribute("logout", true);
		}
		
		return "index";
	}
	
	/* dashboard */
	@RequestMapping(value="/dashboard", method=RequestMethod.GET) 
	public String getDashboardPage(Principal p, Model model) {
		
		model.addAttribute("user", p.getName());
		
		return "dashboard";
	}
	
	/* Site crawler main page */
	@RequestMapping(value="/siteCrawler", method=RequestMethod.GET) 
	public String getDashboardPage(Principal p, Model model,
			@RequestParam(value="page", required=false) Integer pageNum,
			@RequestParam(value="center", required=false) String center) {
		
		// principal will always exist as this page requires ROLE_USER/ADMIN
		
		// may want to refactor and just ask DB for these
		String[] bd2kCenters = {"BDDS", "BDTG", "CCD", "CEDAR", "CPCP", "ENIGMA",
				"HeartBD2K", "KnowEng", "LINCS-DCIC", "LINCS-TG", "MD2K", "Mobilize",
				"PIC-SURE"};
				
		int resultsPerPage = 20;	//results to show per page
		
		// default to first page
		if (pageNum == null) {
			pageNum = 1;
		}
		
		if (center == null) {
			center = "all";
		}
		
		// get default results, most recent 20
		List<Page> results;
		
		// if all centers are desired
		if (center.equals("all")) {
			
			results = pageService.getAllPagesLimOff(resultsPerPage, 
					resultsPerPage*(pageNum-1));
		} else {	
			
			// only grab results for the given center
			results = pageService.getPagesByCenterIDLimOff(resultsPerPage,
					resultsPerPage*(pageNum-1), center);
		}
		
		// set model attributes for view
		model.addAttribute("bd2kCenters", bd2kCenters);
		// model.addAttribute("grantList", grantList);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("results", results);
		model.addAttribute("chosenCenter", center);
		model.addAttribute("user", p.getName());
		
		return "siteCrawler";
	}
	
	/* Publication crawler main page */
	@RequestMapping(value="/pubCrawler")
	public String getPubCrawler(
			Principal p, 
			Model model,
			@RequestParam(value="center", required=false) String center) {
				
		if(center == null) {
			center = "all";
		}
		
		// get center ids
		List<Center> centers = centerService.getAllCenters();
		String[] centerIDs = new String[centers.size()];
		
		for (int i = 0; i < centerIDs.length; i++) {
			centerIDs[i] = centers.get(i).getCenterID();
		}
		
		List<PublicationResult> results;
		
		if(center.equals("all")) {
			// get results
			results = 
					publicationService.getAllPublicationResults();
		} else {
			results = new ArrayList<PublicationResult>();
			results.add(publicationService.getPublicationResultByCenterID(center));
		}

		// set model attributes
		model.addAttribute("user", p.getName());
		model.addAttribute("bd2kCenters", centerIDs);
		model.addAttribute("chosenCenter", center);
		model.addAttribute("results", results);
		
		return "publicationCrawler";
	}
	
	/* results */
	@RequestMapping(value="/digestResults")
	public String getDigestResults(Principal principal,
			HttpServletResponse res,
			Model model, 
			@RequestParam(value = "id", required=false) String id,
			@RequestParam(value="pmid", required = false) String pmid,
			@RequestParam(value="center", required= false) String center) {
	
		if (id != null) {
			// the id is the same as the value in _id, and we want a Page
			Page p = pageService.getPageByID(id);
			model.addAttribute("page", p);
			model.addAttribute("type", "page");
		} else if (pmid != null && center != null){
			// we want a publication result
			Publication pub = null;
			PublicationResult pr = publicationService.getPublicationResultByCenterID(center);
			
			if (pr != null) {
				
				// find the requested publication record
				for(Publication p : pr.getFullContent()) {
					if (p.getPmid().equals(pmid)) {
						pub = p;
						break;
					}
				}
				
				model.addAttribute("publication", pub);
				model.addAttribute("type", "publication");
				model.addAttribute("center", center);
				model.addAttribute("lastCrawlTime", pr.getLastCrawlTime());
			} else {
				res.setStatus(404);
			}
		} else {
			// how shall we handle 404s?
			res.setStatus(404);
		}
		
		model.addAttribute("user", principal.getName());
		
		return "digest";
	}
}

