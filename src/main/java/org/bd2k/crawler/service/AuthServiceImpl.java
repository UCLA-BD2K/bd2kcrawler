package org.bd2k.crawler.service;

import org.bd2k.crawler.model.User;
import org.springframework.stereotype.Service;

/**
 * Placeholder implementation of authentication, no DB calls --
 * @author allengong
 *
 */
@Service("authService")
public class AuthServiceImpl implements AuthService {
	
	/* verify the user */
	public boolean verifyUser(User user) {
		return true;
	}
}
