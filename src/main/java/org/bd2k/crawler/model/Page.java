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
	
	private String url;

	/* ctrs */
	public Page() {}
	
	public Page(String url) {
		this.url = url;
	}
	
	/* Getters and setters */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getURL() {
		return url;
	}

	public void setPageURl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return String.format("[ id: %s, pageURL:%s]", this.id, this.url);
	}
	
	//connectivity test
	public String ping() {
		return "[Page Model] I am alive";
	}
	
}
