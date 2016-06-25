package org.bd2k.crawler.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a document from the Centers collection.
 * @author allengong
 *
 */
@Document(collection="Centers")
public class Center {
	
	// members
	@Id
	private String id;

	private String centerID;
	private String grant;
	private String siteURL;
	
	// cstrs
	public Center() {}
	public Center(String id, String centerID, String grant, String siteURL) {
		super();
		this.id = id;
		this.centerID = centerID;
		this.grant = grant;
		this.siteURL = siteURL;
	}
	
	// getters and setters 
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getCenterID() {
		return centerID;
	}
	public void setCenterID(String centerID) {
		this.centerID = centerID;
	}
	public String getGrant() {
		return grant;
	}
	public void setGrant(String grant) {
		this.grant = grant;
	}
	public String getSiteURL() {
		return siteURL;
	}
	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}	
}
