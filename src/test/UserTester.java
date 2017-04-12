package test;

import ticketing.User;
import junit.framework.TestCase;

public class UserTester extends TestCase {

	public UserTester(String name) {
		super(name);
	}

	public void testUser1() {
		User aUser = new User("Franco", User.UserClass.Maintainer);
		assertEquals("initialization", "Franco", aUser.getUserName());
	}

	public void testUser2() {
		User aUser = new User("Franco", User.UserClass.Maintainer);
		assertEquals("test toString", "Franca", aUser.toString());
	}

}
