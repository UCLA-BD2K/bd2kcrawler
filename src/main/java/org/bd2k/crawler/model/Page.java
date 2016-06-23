package org.bd2k.crawler.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity representing a document for a webpage.
 * @author allengong
 *
 */
@Document(collection="Pages")
public class Page {
	
	@Id
	private String id;
	
	String pageURL;

	/* ctrs */
	public Page() {}
	
	public Page(String URL) {
		this.pageURL = URL;
	}
	
	/* Getters and setters */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPageURl() {
		return pageURL;
	}

	public void setPageURl(String pageURl) {
		this.pageURL = pageURL;
	}
	
	@Override
	public String toString() {
		return String.format("[ id: %s, pageURL:%s]", this.id, this.pageURL);
	}
	
	//functionality test
	public String ping() {
		return "[Page Model] I am alive";
	}
	
}
