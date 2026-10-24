package model;

public class User {
	private int id;
	private String fullname;
	private String username;
	private String email;
	private String password;

	// This code acts as the default constructor
	public User() {
	}

	// This code acts as a constructor for username and password only
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	// This code acts as a constructor for all fields
	public User(String fullname, String username, String email, String password) {
		this.fullname = fullname;
		this.username = username;
		this.email = email;
		this.password = password;
	}

	// Getters
	public String getFullname() {
		return fullname;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	// Setters
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}