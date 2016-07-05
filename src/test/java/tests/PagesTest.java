package tests;

import static org.junit.Assert.fail;

import java.util.List;

import org.bd2k.crawler.model.Page;
import org.bd2k.crawler.service.PageServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
		config.TestConfig.class
		})
public class PagesTest {
		
	@Autowired
	private WebApplicationContext wac;
	
	// services
	private PageServiceImpl pageService;
	
	// for thorough controller testing
	private MockMvc mockMvc;
	
	// globally defined constants
	private static final String TEST_CONFIG = "config.MongoConfigTest";
	
	private String testUrl = "test-item.com";
	private String testUrl2 = "test-item2.com";
	private String testUrl3 = "test-item3.com";
	
	private String testCenter = "TestCenter";
	private String testCenter2 = "TestCenter2";
		
	// before and after each unit test
	@Before 
	public void setUp() {

		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		
		// use test properties for db while testing
		pageService = new PageServiceImpl();
		pageService.setMongoConfigContext(TEST_CONFIG);
		
		// populate with mock data
		initPages();
	}
	
	@After
	public void tearDown() {
		// empty
	}
	
	// service level tests
	
	@Test
	public void testGetAllPages() {
		
		// test pageService: grabbing all pages
		List<Page> pages = pageService.getAllPages();
		Assert.notNull(pages, "There must be valid pages returned");
		
		// see initPages()
		boolean foundUrl = false;
		boolean foundUrl2 = false;
		
		for(Page p : pages) {
			if(p.getUrl() == null) {
				continue;
			}
			if(p.getUrl().equals(testUrl)) {
				foundUrl = true;
			}
			else if(p.getUrl().equals(testUrl2)) {
				foundUrl2 = true;
			}
		}
		
		if(!(foundUrl && foundUrl2)) {
			fail("Expected Pages not found!");
		}
		
		if(pages != null) {
			System.out.println("Page exists??: " + pages.get(0).getUrl());
		}
	}
	
	@Test
	public void testGetPageByCenter() {
		
		List<Page> pages = pageService.getPagesByCenterID(testCenter);
		Assert.isTrue(pages.size() == 2, 
				"There must be 2 pages with center: " + testCenter);
		
		boolean foundP1 = false;
		boolean foundP2 = false;
		
		for(Page p : pages) {
			if(p.getUrl().equals(testUrl)) {
				foundP1 = true;
			}
			else if(p.getUrl().equals(testUrl2)) {
				foundP2 = true;
			}
		}
		
		if(!(foundP1 && foundP2)) {
			fail("Expected Pages not found!");
		}
		
		pages = pageService.getPagesByCenterID("NOT A REAL CENTER");
		Assert.isTrue(pages.size() == 0, 
				"There are not any pages with fake center id");
	}
	
	@Test
	public void testGetAllPagesLimOff() {
		
		int limit = 2, offset = 1;
		List<Page> pages = pageService.getAllPagesLimOff(limit, offset);
		Assert.isTrue(pages.size() == 2, "There must be 2 elements when limit = 2");
		
		// the last page added should be the skipped one -- offset=1
		boolean foundP1 = false;
		boolean foundP2 = false;
		
		for(Page p : pages) {
			if(p.getUrl().equals(testUrl)) {
				foundP1 = true;
			}
			else if(p.getUrl().equals(testUrl2)) {
				foundP2 = true;
			}
		}
		
		if(!(foundP1 && foundP2)) {
			fail("Expected Pages not found!");
		}
		
		pages = pageService.getAllPagesLimOff(1, 3);	// impossible
		Assert.isTrue(pages.size() == 0, "There must not be more than 3 pages");
	}
	
	@Test
	public void testGetPagesByCenterLimOff() {
		
		int limit = 2, offset = 1; // get only 2, skip one
		List<Page> pages = pageService.getPagesByCenterIDLimOff(limit, 
				offset, testCenter);
		
		Assert.isTrue(pages.size() == 1, "There must be 1 result");
		for(Page p : pages) {
			if(!(p.getCenterID().equals(testCenter))) {
				fail("Incorrect Page returned by centerID!");
			}
			if(!(p.getUrl().equals(testUrl))) {
				fail("Incorrect Page returned by URL");
			}
		}
	}
	
	@Test
	public void testGetPageByURLCenter() {
		
		Page p = pageService.getPageByURLandCenterId(testUrl3, 
				testCenter2);
		
		Assert.isTrue(p != null, "Page must exist");
		Assert.isTrue(p.getCenterID().equals(testCenter2));
		Assert.isTrue(p.getUrl().equals(testUrl3));
	}
	
	@Test
	public void testUpdatePage() {
		
		Page p = pageService.getPageByURLandCenterId(testUrl, testCenter);
		p.setCenterID(testCenter2);
		
		pageService.saveOrUpdatePage(p);
		p = pageService.getPageByURLandCenterId(testUrl, testCenter2);
		Assert.isTrue(p != null);
		Assert.isTrue(p.getUrl().equals(testUrl));
		Assert.isTrue(p.getCenterID().equals(testCenter2));
		
		p.setCenterID(testCenter);
		pageService.saveOrUpdatePage(p);
	}
  	
	
	/* Helpers - mainly mock data creation */
	// assumes saveOrUpdatePage() is implemented correctly
	private void initPages() {
		
		// test pages, lastCrawlTimes are 1 day apart
		Page p = new Page();
		p.setUrl(testUrl);
		p.setCenterID(testCenter);
		p.setLastCrawlTime("2016-07-05T14:16:37.379-0700");
		
		Page p2 = new Page();
		p2.setUrl(testUrl2);
		p2.setCenterID(testCenter);
		p2.setLastCrawlTime("2016-07-06T14:16:37.379-0700");
		
		Page p3 = new Page();
		p3.setUrl(testUrl3);
		p3.setCenterID(testCenter2);
		p3.setLastCrawlTime("2016-07-07T14:16:37.379-0700");
		
		pageService.saveOrUpdatePage(p);		// always use saveOrUpdate, to not overpopulate the DB
		pageService.saveOrUpdatePage(p2);
		pageService.saveOrUpdatePage(p3);
	}
}
