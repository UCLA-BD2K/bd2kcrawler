package org.bd2k.crawler.model;

public class ArchivedPage {
	private int pageId;

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	
	public String ping() {
		return "[Archived Page Model] I am alive";
	}
	
}
