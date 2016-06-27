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
	
	private String lastCrawlTime;
	private String lastDiff;
	private String currentContent;
	private String url;
	private String centerID;


	/* ctrs */
	public Page() {}
	
	public Page(String lastCrawlTime, String lastDiff, String currentContent, String url,
			String centerID) {
		super();
		this.lastCrawlTime = lastCrawlTime;
		this.lastDiff = lastDiff;
		this.currentContent = currentContent;
		this.url = url;
		this.centerID = centerID;
	}
	
	/* Getters and setters */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getLastCrawlTime() {
		return lastCrawlTime;
	}

	public void setLastCrawlTime(String lastCrawlTime) {
		this.lastCrawlTime = lastCrawlTime;
	}

	public String getLastDiff() {
		return lastDiff;
	}

	public void setLastDiff(String lastDiff) {
		this.lastDiff = lastDiff;
	}

	public String getCurrentContent() {
		return currentContent;
	}

	public void setCurrentContent(String currentContent) {
		this.currentContent = currentContent;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCenterID() {
		return centerID;
	}

	public void setCenterID(String centerID) {
		this.centerID = centerID;
	}
	
	/* other */

	@Override
	public String toString() {
		return String.format("[ id: %s, pageURL:%s]", this.id, this.url);
	}
	
	//connectivity test
	public String ping() {
		return "[Page Model] I am alive";
	}
	
}
