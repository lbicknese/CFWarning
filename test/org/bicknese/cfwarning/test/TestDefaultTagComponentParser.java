package org.bicknese.cfwarning.test;

import org.junit.Test;

public class TestDefaultTagComponentParser extends AbstractTest {

	@Test
	public void testNormal() {
		testParse("tag.yml", true);
	}
	
	@Test
	public void testEmptyFile() {
		testParse("emptyTag.yml", true);
	}
	
	@Test
	public void testTagNoFunction() {
		testParse("tagNoFunction.yml", true);
	}
	
}
