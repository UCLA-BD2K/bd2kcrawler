package org.bd2k.crawler.model;

/**
 * Entity representing a document in the Publications collection.
 * @author allengong
 *
 */
public class Publication {
	
	//@Id
	//private String id;
	
	private String title;
	private String pmid;
	private String pubDate;
	private String[] authors;
	private String journal;
	
	/* cstrs */
	public Publication(){}

	public Publication(String title, String date, String[] authors, String journal) {
		super();
		this.title = title;
		this.pubDate = date;
		this.authors = authors;
		this.journal = journal;
	}
	
	
	/* getters and setters */
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPmid() {
		return pmid;
	}

	public void setPmid(String pmid) {
		this.pmid = pmid;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String date) {
		this.pubDate = date;
	}

	public String[] getAuthors() {
		return authors;
	}

	public void setAuthors(String[] authors) {
		this.authors = authors;
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}
}
