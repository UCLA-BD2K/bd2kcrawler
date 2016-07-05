package org.bd2k.crawler.service;

import java.util.List;

import org.bd2k.crawler.model.Publication;
import org.bd2k.crawler.model.PublicationResult;

/**
 * Interface for services that handle needs for archived publications.
 * Also includes services for PublicationResult.
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
	 * will be made.
	 * @param p the publication document
	 */
	public void savePublication(Publication p);
	
	/**
	 * Same as savePublication() except that a check if made 
	 * to see if there is an existing document - if so update it.
	 * @param p the publication document
	 */
	public void saveOrUpdatePublication(Publication p);
	
	/**
	 * Retrieves all Publication Results, indexed by CenterID.
	 * @return a list of PublicationResult objects.
	 */
	public List<PublicationResult> getAllPublicationResults();
	
	/**
	 * Retrieves a PublicationResult by its center id.
	 * @param id the center id
	 * @return a PublicationResult object with matching center id, or null.
	 */
	public PublicationResult getPublicationResultByCenterID(String id);
	
	/**
	 * Same as savePublication(), but for PublicationResult.
	 * As before, no additional checks are made before saving, so
	 * this is a less safe function than saveOrUpdateX().
	 * @param p the publication result document
	 */
	public void savePublicationResult(PublicationResult p);
	
	/**
	 * Same as saveOrUpdatePublication(), but for PublicationResult.
	 * @param p the publication result document
	 */
	public void saveOrUpdatePublicationResult(PublicationResult p);
}
