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
	 * Retrieves archived pages from previous crawls by center id.
	 * @return a list of Page objects
	 */
	public List<Page> getPagesByCenterID();
	
	/**
	 * Retrieves all archived pages.
	 * @return a list of all pages in the form of Page objects
	 */
	public List<Page> getAllPages();
	
	/**
	 * Retrieves all pages under the given limit and offset.
	 * @param limit number of pages to return
	 * @param offset number of pages to skip (organized by more frequent first)
	 * @return a list of Page objects
	 */
	public List<Page> getAllPagesLimOff(int limit, int offset);
}
