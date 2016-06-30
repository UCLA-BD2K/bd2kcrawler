package org.bd2k.crawler.model;

import org.springframework.data.annotation.Id;

/**
 * Entity representing a document in the Publications collection.
 * @author allengong
 *
 */
public class Publication {
	
	@Id
	private String id;
	
	private String title;
	private String date;
	private String[] authors;
	private String journal;
	private String[] centers;
	
	/* cstrs */
	public Publication(){}

	public Publication(String title, String date, String[] authors, String journal, String[] centers) {
		super();
		this.title = title;
		this.date = date;
		this.authors = authors;
		this.journal = journal;
		this.centers = centers;
	}
	
	
	/* getters and setters */
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String[] getCenters() {
		return centers;
	}

	public void setCenters(String[] centers) {
		this.centers = centers;
	}
}
