package ticketing;

import java.util.HashSet;
import java.util.Set;


public class User {
	private String userName;
	private Set<UserClass> roles = new HashSet<>();
	
	static public enum UserClass {
		Reporter, Maintainer;

	}

	public User(String userName, UserClass role) {
		this.userName = userName;
		roles.add(role);
	}
	public User(String userName, Set<UserClass> role) {
		this.userName = userName;
		roles.addAll(role);
	}
	
	Set<UserClass> getUserClasses() {
		return roles;
	}
	public String getUserName() {
		return userName;
	}
	
	
	
}
