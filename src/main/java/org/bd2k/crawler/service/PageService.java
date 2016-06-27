package org.bd2k.crawler.service;

import java.util.List;

import org.bd2k.crawler.model.Page;

/**
 * Interface for the service to handle needs for previously archived 
 * crawls. 
 * 
 * @author allengong
 *
 */
public interface PageService {
	
	public String ping();
	
	/**
	 * Retrieves a specific page 
	 * @param id the id of the page
	 * @return The page with the matching id
	 */
	public Page getPageByID(String id);
	
	/**
	 * Retrieves archived pages from previous crawls by center id.
	 * @param id the center id
	 * @return a list of Page objects
	 */
	public List<Page> getPagesByCenterID(String id);
	
	/**
	 * Retrieves all archived pages.
	 * @return a list of all pages in the form of Page objects
	 */
	public List<Page> getAllPages();
	
	/**
	 * Retrieves all pages under the given limit and offset, more frequent
	 * first.
	 * @param limit max number of pages to return
	 * @param offset number of pages to skip
	 * @return a list of Page objects
	 */
	public List<Page> getAllPagesLimOff(int limit, int offset);
	
	/**
	 * Same as getAllPagesLimOff, except with an additional constraint of
	 * center id.
	 * @param limit max number of pages to return
	 * @param offset number of pages to skip
	 * @param id the center id of which the pages were retrieved from
	 * @return
	 */
	public List<Page> getPagesByCenterIDLimOff(int limit, int offset, 
			String id);
	
	/**
	 * Returns the first matching page in DB by url and center id.
	 * @param url url the page was recorded from
	 * @param id the center id
	 * @return
	 */
	public Page getPageByURLandCenterId(String url, String id);
	
	/**
	 * Saves the given page to the DB, no extra checks are made.
	 * Use if you already query the DB for a page, and if you handle
	 * updates if needed.
	 * @param p the page to store.
	 */
	public void savePage(Page p);
	
	/**
	 * Saves the given page into the DB. If the page (determined by
	 * center id and url combination) already exists, this will update
	 * the contents of that Document. This is a safer wrapper over
	 * savePage().
	 * @param p the page to store/update.
	 */
	public void saveOrUpdatePage(Page p);
}
