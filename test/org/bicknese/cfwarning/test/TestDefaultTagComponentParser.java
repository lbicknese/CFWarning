package org.bicknese.cfwarning.test;

import org.junit.Test;

public class TestDefaultTagComponentParser extends AbstractTest {

	@Test
	public void testNormal() {
		testParse("tag.yml");
	}
	
	@Test
	public void testEmptyFile() {
		testParse("emptyTag.yml");
	}
	
	@Test
	public void testTagNoFunction() {
		testParse("tagNoFunction.yml");
	}
	
}
