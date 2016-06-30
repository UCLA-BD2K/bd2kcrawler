package org.bd2k.crawler.service;

import org.bd2k.crawler.model.Publication;

/**
 * Interface for services that handle needs for archived publications.
 * @author allengong
 *
 */
public interface PublicationService {
	
	/**
	 * Retrieves a publication by its _id value.
	 * @param id the id
	 * @return the publication with matching id
	 */
	public Publication getPublicationByID(String id);
	
	/**
	 * Same as above, but using pmid as the id value.
	 * @param pmid the pmid
	 * @return the publication with matching pmid
	 */
	public Publication getPublicationByPmid(String pmid);
	
	/**
	 * Stores the publication record into the database. No checks
	 * will be made
	 * @param p the publication document
	 */
	public void savePublication(Publication p);
	
	/**
	 * Same as savePublication() except that a check if made 
	 * to see if there is an existing document - if so update it.
	 * @param p the publication document
	 */
	public void saveOrUpdatePublication(Publication p);
}
