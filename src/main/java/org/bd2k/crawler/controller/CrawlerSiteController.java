package org.bd2k.crawler.controller;

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
	public String getHomePage() {
		return "index";
	}
	
	/* dashboard */
	@RequestMapping(value="/dashboard", method=RequestMethod.GET) 
	public String getDashboardPage(Model model) {
		//probably doesn't belong here, save for refactoring later
//		HashMap<String, String> bd2kCenters = new HashMap<String, String>();
//		bd2kCenters.put("BDDS", "https://bd2kccc.org/index.php/bdds");
//		bd2kCenters.put("BDTG", "https://bd2kccc.org/index.php/bdtg");
//		bd2kCenters.put("CCD", "https://bd2kccc.org/index.php/ccd");
//		bd2kCenters.put("CEDAR", "https://bd2kccc.org/index.php/cedar");
//		bd2kCenters.put("CPCP", "https://bd2kccc.org/index.php/cpcp");
//		bd2kCenters.put("ENIGMA", "https://bd2kccc.org/index.php/enigma");
//		bd2kCenters.put("HeartBD2K", "https://bd2kccc.org/index.php/heartbd2k");
//		bd2kCenters.put("KnowEng", "https://bd2kccc.org/index.php/knoweng");
//		bd2kCenters.put("LINCS-DCIC", "https://bd2kccc.org/index.php/lincs-dcic");
//		bd2kCenters.put("LINCS-TG", "https://bd2kccc.org/index.php/lincs-tg");
//		bd2kCenters.put("MD2K", "https://bd2kccc.org/index.php/md2k");
//		bd2kCenters.put("Mobilize", "https://bd2kccc.org/index.php/mobilize");
//		bd2kCenters.put("PIC-SURE", "https://bd2kccc.org/index.php/picsure");
		
		String[] bd2kCenters = {"BDDS", "BDTG", "CCD", "CEDAR", "CPCP", "ENIGMA",
				"HeartBD2K", "KnowEng", "LINCS-DCIC", "LINCS-TG", "MD2K", "Mobilize",
				"PIC-SURE"};
		
		String[] grantList = { "EB020406", "HG007990", "HG008540", "AI117925", 
				"AI117924", "EB020403", "GM114833", "GM114838", "HL127624", 
				"HL127366", "EB020404", "EB020405", "HG007963"};
		
		model.addAttribute("bd2kCenters", bd2kCenters);
		model.addAttribute("grantList", grantList);
		
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
	
	// other
	//should be in its own controller
	@RequestMapping(value="/login", method=RequestMethod.POST) 
	public String loginUser(HttpServletRequest req) {
		System.out.println("loginUser()");
		
		if(authService.verifyUser(new User(req.getParameter("username"),
				req.getParameter("password"))))
			return "redirect:dashboard";
			
		return "redirect:index";	//fail auth
	}
}

