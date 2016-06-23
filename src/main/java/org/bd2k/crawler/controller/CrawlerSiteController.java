package org.bd2k.crawler.controller;

import javax.servlet.http.HttpServletRequest;

import org.bd2k.crawler.model.User;
import org.bd2k.crawler.service.AuthService;
import org.bd2k.crawler.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller that handles requests for site pages.
 * Not to be confused with CrawlerAPIController, which exposes API endpoints.
 * @author allengong
 *
 */
@Controller
public class CrawlerSiteController {
	@Autowired
	private PageService archiveService;
	
	@Autowired
	private AuthService authService;

	/* for testing functionality */
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public String getTest() {
		
		System.out.println(archiveService.ping());
		
		return "test";
	}
	
	/* homepage */
	@RequestMapping(value="/index", method=RequestMethod.GET) 
	public String getHomePage() {
		return "index";
	}
	
	/* dashboard */
	@RequestMapping(value="/dashboard", method=RequestMethod.GET) 
	public String getDashboardPage() {
		return "dashboard";
	}
	
	
	// other
	
	@RequestMapping(value="/login", method=RequestMethod.POST) 
	public String loginUser(HttpServletRequest req) {
		System.out.println("loginUser()");
		
		if(authService.verifyUser(new User(req.getParameter("username"),
				req.getParameter("password"))))
			return "redirect:dashboard";
			
		return "redirect:index";	//fail auth
	}
}

