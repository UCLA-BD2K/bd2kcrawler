package org.bd2k.crawler.service;

import org.bd2k.crawler.model.User;

/**
 * Service for authentication.
 * @author allengong
 *
 */
public interface AuthService {
	
	/* given a user object with at least username+pass, verify */
	public boolean verifyUser(User user);
}
