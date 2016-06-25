package org.bd2k.crawler.service;

import java.util.List;

import org.bd2k.crawler.model.Center;

/**
 * Represents a service for handling data for the Centers collection.
 * @author allengong
 *
 */
public interface CenterService {

	/**
	 * Retrieves a center given its center ID, e.g. LINCS-DCIC
	 * @param id
	 * @return a center object containing information on the center.
	 */
	public Center getCenterByID(String id);
	
	/**
	 * Retrieves all centers stored in the database.
	 * @return a list of centers and their info
	 */
	public List<Center> getAllCenters();

}
