package org.bd2k.crawler.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entitiy for representing a document in PublicationResults.
 * @author allengong
 *
 */
@Document(collection="PublicationResults")
public class PublicationResult {

	@Id
	private String id;
	
	private String centerID;
	private String lastCrawlTime;
	private String[] currentContent;
	private String[] lastDiff;
	private Publication[] fullContent;
	
	public PublicationResult(){}
	
	public PublicationResult(String centerID, String lastCrawlTime, String[] currentContent, 
			String[] lastDiff,
			Publication[] fullContent) {
		super();
		this.centerID = centerID;
		this.lastCrawlTime = lastCrawlTime;
		this.currentContent = currentContent;
		this.lastDiff = lastDiff;
		this.fullContent = fullContent;
	}
	
	/* Getters and setters */

	public String getCenterID() {
		return centerID;
	}

	public void setCenterID(String centerID) {
		this.centerID = centerID;
	}

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

	public String[] getCurrentContent() {
		return currentContent;
	}

	public void setCurrentContent(String[] currentContent) {
		this.currentContent = currentContent;
	}

	public String[] getLastDiff() {
		return lastDiff;
	}

	public void setLastDiff(String[] lastDiff) {
		this.lastDiff = lastDiff;
	}

	public Publication[] getFullContent() {
		return fullContent;
	}

	public void setFullContent(Publication[] fullContent) {
		this.fullContent = fullContent;
	}
}
