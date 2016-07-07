package tests;

import static org.junit.Assert.fail;

import java.util.List;

import org.bd2k.crawler.model.Center;
import org.bd2k.crawler.service.CenterServiceImpl;
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
public class CentersTest {
	
	@Autowired
	private WebApplicationContext wac;
	
	// services
	private CenterServiceImpl centerService;
	
	// for thorough controller testing
	private MockMvc mockMvc;
	
	// globally defined constants
	private static final String TEST_CONFIG = "config.MongoConfigTest";
	
	private String testUrl = "test-item.com";
	private String testUrl2 = "test-item2.com";
	
	private String testCenter = "TestCenter";
	private String testCenter2 = "TestCenter2";
	
	private String grant = "grant1";
	private String grant2 = "grant2";
	
	// before and after each unit test
	@Before 
	public void setUp() {

		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		
		// use test properties for db while testing
		centerService = new CenterServiceImpl();
		centerService.setMongoConfigContext(TEST_CONFIG);
		
		// populate with mock data
		initCenters();
	}
	
	@After
	public void tearDown() {
		// empty
	}
	
	@Test
	public void testGetCenterByCenterID() {
		
		Center c = centerService.getCenterByID("Not A CENTER");
		Assert.isTrue(c == null);
		
		c = centerService.getCenterByID(testCenter);
		Assert.isTrue(c != null);
		Assert.isTrue(c.getCenterID().equals(testCenter));
		Assert.isTrue(c.getSiteURL().equals(testUrl));
		Assert.isTrue(c.getGrant().equals(grant));
	}
	
	@Test
	public void testGetAllCenters() {
		
		List<Center> centers = centerService.getAllCenters();
		Assert.isTrue(centers != null);
		Assert.isTrue(centers.size() == 2);
		
		boolean foundC1 = false;
		boolean foundC2 = false;
		for(Center c : centers) {
			if(c.getCenterID().equals(testCenter)) {
				foundC1 = true;
			}
			else if(c.getCenterID().equals(testCenter2)) {
				foundC2 = true;
			}
		}
		
		if(!(foundC1 && foundC2)) {
			fail("Expected Centers not found!");
		}
		
	}
	
	// mock data
	private void initCenters() {
		
		Center c = new Center();
		c.setCenterID(testCenter);
		c.setGrant(grant);
		c.setSiteURL(testUrl);
		
		Center c2 = new Center();
		c2.setCenterID(testCenter2);
		c2.setGrant(grant2);
		c2.setSiteURL(testUrl2);
		
		centerService.saveOrUpdateCenter(c);
		centerService.saveOrUpdateCenter(c2);
	}
}
