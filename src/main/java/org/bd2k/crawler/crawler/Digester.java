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
	
	/**
	 * Left-hand string to use as base.
	 */
	private String stringOne;
	
	/**
	 * Right-hand string to use as comparison to the base.
	 */
	private String stringTwo;
	
	/**
	 * Delegation object to apply diff-match-patch.
	 */
	diff_match_patch digest = new diff_match_patch();
	
	/**
	 * No-arg constructor.
	 */
	public Digester() {
		super();
		this.stringOne = "";
		this.stringTwo = "";
	}
	
	/**
	 * Constructor taking in strings to compare.
	 * @param stringOne the base string
	 * @param stringTwo the string to compare against the base string.
	 */
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
	
	/** 
	 * Returns a human readable "diff" in HTML format.
	 * */
	public String computeHTMLDiff() {
		LinkedList<Diff> diffs = digest.diff_main(this.stringOne, this.stringTwo);
		digest.diff_cleanupSemantic(diffs);
		return digest.diff_prettyHtml(diffs);
	}
	
	
}
