package org.bd2k.crawler.crawler;

import java.util.LinkedList;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

/**
 * Represents a digester object that will perform a simple 
 * comparison between 2 Java Strings.
 * 
 * Utilizes a wrapper of Neil Fraser's diff-match-patch
 * @author allengong
 *
 */
public class Digester {
	
	// 2 input strings
	private String stringOne;
	private String stringTwo;
	
	// delegation
	diff_match_patch digest = new diff_match_patch(null);
	
	// cstrs
	public Digester() {
		super();
		this.stringOne = "";
		this.stringTwo = "";
	}
	public Digester(String stringOne, String stringTwo) {
		super();
		this.stringOne = stringOne;
		this.stringTwo = stringTwo;
	}
	
	// getters and setters
	public String getStringOne() {
		return stringOne;
	}
	public void setStringOne(String stringOne) {
		this.stringOne = stringOne;
	}
	public String getStringTwo() {
		return stringTwo;
	}
	public void setStringTwo(String stringTwo) {
		this.stringTwo = stringTwo;
	}
	
	// functions to compute a human readable diff
	
	/* returns HTML string, auto-generated of the diffs */
	public String computeHTMLDiff() {
		
		LinkedList<Diff> diffs = digest.diff_main(this.stringOne, this.stringTwo);
		
		return digest.diff_prettyHtml(diffs);
		

	}
	
	
}
