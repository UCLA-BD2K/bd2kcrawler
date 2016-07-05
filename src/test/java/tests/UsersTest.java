package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
		config.TestConfig.class
		})
public class UsersTest {

	//TODO when the registration process is set up, right now there is little point in testing
	
	@Test
	public void testNothing() {
		
	}
}
