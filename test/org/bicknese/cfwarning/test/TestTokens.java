package org.bicknese.cfwarning.test;

import junit.framework.TestCase;

import org.bicknese.cfwarning.Tokens;
import org.junit.*;

public class TestTokens extends TestCase {
	
	@Test
    public void testEmptyFile() {
        Tokens tokens = new Tokens("");
        assertEquals(tokens.getNextToken(),null);
        assertEquals(tokens.getCurrentLineNumber(),1);
    }
	
	@Test
	public void testStructNotation() {
		Tokens tokens = new Tokens("arg[\"value\"] = 1;");
		assertEquals("arg", tokens.getNextToken());
		assertEquals("[", tokens.getNextToken());
		assertEquals("\"", tokens.getNextToken());
		assertEquals("value", tokens.getNextToken());
		assertEquals("\"", tokens.getNextToken());
		assertEquals("]", tokens.getNextToken());
		assertEquals("=", tokens.getNextToken());
		assertEquals("1", tokens.getNextToken());
		assertEquals(";", tokens.getNextToken());
	}
	
}
