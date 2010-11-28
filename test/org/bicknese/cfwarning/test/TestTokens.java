package org.bicknese.cfwarning.test;

import org.bicknese.cfwarning.Tokens;
import org.junit.*;

import static org.junit.Assert.*;

public class TestTokens {
	
	@Test
    public void testEmptyFile() {
        Tokens tokens = new Tokens("");
        assertEquals(tokens.getNextToken(),null);
        assertEquals(tokens.getCurrentLineNumber(),1);
    }
	
}
