package org.bicknese.cfwarning.test;

import org.junit.Test;

public class TestDefaultScriptComponentParser extends AbstractTest {

	@Test
	public void testNormal() {
		testParse("script.yml", false);
	}
	
	@Test
	public void testEmptyFile() {
		testParse("emptyScript.yml", false);
	}
	
	@Test
	public void testTagNoFunction() {
		testParse("scriptNoFunction.yml", false);
	}
	
}
