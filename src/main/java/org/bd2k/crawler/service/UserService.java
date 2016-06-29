package org.bd2k.crawler.service;

import org.bd2k.crawler.model.User;

/**
 * Service for handling requests on the Users collection.
 * @author allengong
 *
 */
public interface UserService {
	
	/**
	 * Retrieves a given user based on email address (username)
	 * @param email
	 * @return a User object with the data needed
	 */
	public User getUserByEmail(String email);
}
