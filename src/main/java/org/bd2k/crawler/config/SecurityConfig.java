package org.bd2k.crawler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Deprecated, we use XML config now -- see security-config.xml
 * @author allengong
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/*@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	public void configAuthBuilder(AuthenticationManagerBuilder b) throws Exception {
		b.userDetailsService(userDetailsService);
	}*/
	
	//mkyong way
	@Autowired
	public void configAuthBuilder(AuthenticationManagerBuilder b) throws Exception {
		b.inMemoryAuthentication().withUser("test@email.com").password("123").roles("USER");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		.antMatchers("/dashboard").access("hasRole('ROLE_USER')")
		.and()
			.formLogin()
				.loginProcessingUrl("/j_spring_security_check")
				.loginPage("/index").failureUrl("/index?error")
			.usernameParameter("email").passwordParameter("password")
		.and()
			.logout().logoutSuccessUrl("/index?logout")
		.and()
			.csrf();
	}
}
