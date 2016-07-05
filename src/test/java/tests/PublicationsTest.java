package tests;

import org.bd2k.crawler.model.Publication;
import org.bd2k.crawler.model.PublicationResult;
import org.bd2k.crawler.service.PublicationServiceImpl;
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
public class PublicationsTest {
	
	@Autowired
	private WebApplicationContext wac;
	
	// services
	private PublicationServiceImpl publicationService;
	
	// for thorough controller testing
	private MockMvc mockMvc;
	
	// globally defined constants
	private static final String TEST_CONFIG = "config.MongoConfigTest";
	private String testTitle = "test title";
	private String pmid = "pmid1";
	private String center = "TestCenter";
	
	// before and after each unit test
	@Before 
	public void setUp() {

		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		
		// use test properties for db while testing
		publicationService = new PublicationServiceImpl();
		publicationService.setMongoConfigContext(TEST_CONFIG);
		
		// populate with mock data
		initPublications();
	}
	
	@Test
	public void testGetPublicationByPmid() {
		
		Publication p = publicationService.getPublicationByPmid("FAKE");
		Assert.isTrue(p == null);
		
		p = publicationService.getPublicationByPmid(pmid);
		Assert.isTrue(p != null);
		Assert.isTrue(p.getPmid().equals(pmid));
		Assert.isTrue(p.getTitle().equals(testTitle));
		
	}
	
	@Test
	public void testGetPRByCenter() {
		
		PublicationResult pr = publicationService.getPublicationResultByCenterID("FAKE");
		Assert.isTrue(pr == null);
		
		pr = publicationService.getPublicationResultByCenterID(center);
		Assert.isTrue(pr.getCenterID().equals(center));
	}
	
	// mock data
	private void initPublications() {
		
		Publication p  = new Publication();
		p.setPmid(pmid);
		p.setTitle(testTitle);
		
		publicationService.saveOrUpdatePublication(p);
		
		PublicationResult pr = new PublicationResult();
		pr.setCenterID(center);
		pr.setCurrentContent(new String[0]);
		
		publicationService.saveOrUpdatePublicationResult(pr);
	}

}
