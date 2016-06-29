package org.bd2k.crawler.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entity representing a user document
 * @author allengong
 *
 */
@Document(collection="Users")
public class User {
	@Id
	private String id;
	
	private String firstName;
	private String lastName;
	private String password;	//should be hashed
	private String email;
	private String role;
		
	// cstrs
	public User() {}
	public User(String em, String pass) {
		email = em;
		password = pass;
	}
	public User(String fname, String lname, String pass, String emailAdd,
			String r) {
		firstName = fname;
		lastName = lname;
		password = pass;
		email = emailAdd;
		role = r;
	}
	
	
	// getters and setters
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	// for nice printing
	
	@Override
	public String toString() {
		return "[user] " + this.email + " < " + this.firstName + " " + 
				this.lastName + " >";
	}
}
