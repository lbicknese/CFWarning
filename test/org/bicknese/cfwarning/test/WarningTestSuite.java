package org.bicknese.cfwarning.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class WarningTestSuite {
	
	public static Test suite() {
		
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(TestTokens.class);
		
		suite.addTestSuite(TestDefaultTagComponentParser.class);
		
		suite.addTestSuite(TestDefaultScriptComponentParser.class);
		
		return suite;
		
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
}