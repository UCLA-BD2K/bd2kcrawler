package org.bd2k.crawler.dao;

import org.springframework.data.annotation.Id;

/**
 * Entity representing a document for a webpage.
 * @author allengong
 *
 */
public class Page {
	
	@Id
	private String id;
	
	private String pageURl;

	/* ctrs */
	public Page() {}
	
	public Page(String URL) {
		this.pageURl = URL;
	}
	
	/* Getters and setters */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPageURl() {
		return pageURl;
	}

	public void setPageURl(String pageURl) {
		this.pageURl = pageURl;
	}
	
}
