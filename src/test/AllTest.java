package test;

import junit.framework.TestSuite;

public class AllTest extends TestSuite {

	public AllTest(String name) {
		super(name);
	}

	public static TestSuite suite() {

		AllTest alltest1 = new AllTest("test suite 1");
		alltest1.addTestSuite(UserTester.class);
		return alltest1;

	}

}
