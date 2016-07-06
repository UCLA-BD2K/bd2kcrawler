package org.bd2k.crawler.service;

import java.util.ArrayList;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Required implementation for authentication.
 * This is needed if we use any DAO-like class during the
 * process of authenticating users.
 * @author allengong
 *
 */
@Service("secUserDetailsService")
public class SecUserDetailsService implements UserDetailsService {

	private UserService userService = new UserServiceImpl();
	
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		// email is used as username in our app
		org.bd2k.crawler.model.User user = userService.getUserByEmail(email);
		
		if (user == null) {
			throw new UsernameNotFoundException(email);
		}
		
		ArrayList<SimpleGrantedAuthority> auths = new ArrayList<SimpleGrantedAuthority>();
		auths.add(new SimpleGrantedAuthority(user.getRole()));
			
		UserDetails userDetails = 
				new org.springframework.security.core.userdetails.User(
						user.getEmail(), user.getPassword(), auths);
		
		return userDetails;
	}
	
}
