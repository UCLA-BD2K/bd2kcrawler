package org.bd2k.crawler.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bd2k.crawler.model.Page;
import org.bd2k.crawler.model.User;
import org.bd2k.crawler.service.AuthService;
import org.bd2k.crawler.service.PageService;
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
	private AuthService authService;

	/* for testing functionality */
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public String getTest() {
		
		System.out.println(pageService.ping());
		
		return "test";
	}
	
	/* homepage */
	@RequestMapping(value="/index", method=RequestMethod.GET) 
	public String getHomePage(Principal p) {
		
		if(p != null) {
			System.out.println("principal name " + p.getName());
			return "redirect:dashboard";
		}
		
		return "index";
	}
	
	/* dashboard */
	@RequestMapping(value="/dashboard", method=RequestMethod.GET) 
	public String getDashboardPage(Principal p, Model model,
			@RequestParam(value="page", required=false) Integer pageNum,
			@RequestParam(value="center", required=false) String center,
			@RequestParam(value="type", required=false) String type) {
		
		//principal will always exist as this page requires ROLE_USER/ADMIN
		
		//may want to refactor and just ask DB for these
		String[] bd2kCenters = {"BDDS", "BDTG", "CCD", "CEDAR", "CPCP", "ENIGMA",
				"HeartBD2K", "KnowEng", "LINCS-DCIC", "LINCS-TG", "MD2K", "Mobilize",
				"PIC-SURE"};
		
		String[] grantList = { "EB020406", "HG007990", "HG008540", "AI117925", 
				"AI117924", "EB020403", "GM114833", "GM114838", "HL127624", 
				"HL127366", "EB020404", "EB020405", "HG007963"};
		
		int resultsPerPage = 20;	//results to show per page
		
		//default to first page
		if(pageNum == null) {
			pageNum = 1;
		}
		
		if(center == null) {
			center = "all";
		}
		
		if(type == null) {
			type = "sites";
		}
		
		//get default results, most recent 20
		List<Page> results;
		
		//if all centers are desired
		if(center.equals("all")) {
			
			results = pageService.getAllPagesLimOff(resultsPerPage, 
					resultsPerPage*(pageNum-1));
		}
		else {	
			
			//only grab results for the given center
			results = pageService.getPagesByCenterIDLimOff(resultsPerPage,
					resultsPerPage*(pageNum-1), center);
		}
		
		//set model attributes for view
		model.addAttribute("bd2kCenters", bd2kCenters);
		model.addAttribute("grantList", grantList);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("results", results);
		model.addAttribute("chosenCenter", center);
		model.addAttribute("type", type);
		model.addAttribute("user", p.getName());
		
		return "dashboard";
	}
	
	/* results */
	@RequestMapping(value="/digestResults")
	public String getDigestResults(Model model, @RequestParam("id") String id) {
		
		//the id is the same as the value in _id
		Page p = pageService.getPageByID(id);
		model.addAttribute("page", p);
		
		return "digest";
	}
}

