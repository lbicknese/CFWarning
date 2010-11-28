package org.bicknese.cfwarning.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.bicknese.cfwarning.*;

import com.esotericsoftware.yamlbeans.YamlReader;

public class AbstractTest {
	
    public void testParse(String definition) {
		try {
			
	        Map map = getExpectedOutput(definition);

	        ArrayList expectedFunctions = new ArrayList();
	        if(map.containsKey("functions")) {
	        	expectedFunctions = (ArrayList)map.get("functions");
	        }
	        
	        Vector<Function> actualFunctions = getActualOutput((String) map.get("file"));
	        
	        testFunctions(expectedFunctions,actualFunctions);
	        
	        assertTrue(true);
	        
        } catch (Exception e) {
        	e.printStackTrace();
        	assertTrue("Something in the test failed, or the parse failed.",false);
        }
        
    }
	
	private Map getExpectedOutput(String definition) {
		
		try {
			YamlReader reader = new YamlReader(new FileReader(".\\test\\org\\bicknese\\cfwarning\\test\\"+definition));
	        Object object = reader.read();
	        Map map = (Map)object;
	        return map;
		} catch(Exception e) {
			assertTrue("No definition file found.",false);
		}
		
		return null;
        
	}
	
	private Vector<Function> getActualOutput(String f) {
		
		try {
			
			File file = new File(".\\test\\org\\bicknese\\cfwarning\\test\\"+f);
			FileReader fr = new FileReader(file);
	        BufferedReader br = new BufferedReader(fr);
	        String text = "";
	        while(br.ready()) {
	        	text += br.readLine()+"\n";
	        }
	        br.close();
	        fr.close();

	        Tokens tokens = new Tokens(text);
	        Vector<Function> functions = new Vector<Function>();
	        DefaultTagComponentParser parser = new DefaultTagComponentParser(functions);
	        parser.parse(tokens);
	        
	        return functions;
	        
		} catch(Exception e) {
			assertTrue("No file found.",false);
		}
		
        return null;
        
	}
	
	private void testFunctions(ArrayList expectedFunctions,Vector<Function> actualFunctions) {

        assertEquals("Not the same number of functions.",expectedFunctions.size(),actualFunctions.size());
        
        for(int i=0; i < actualFunctions.size(); i++) {
        	
        	Map expectedFunction = (Map) expectedFunctions.get(i);
        	Function actualFunction = actualFunctions.get(i);
        	
        	testFunction(expectedFunction,actualFunction);
        	
        }
        
	}
	
	private void testFunction(Map expectedFunction,Function actualFunction) {

    	assertEquals("Function not found on correct line number.",Integer.parseInt((String) expectedFunction.get("line number")),actualFunction.getLineNumber());
    	assertEquals("Function does not have correct name.",expectedFunction.get("name"),actualFunction.getFunctionName());
    	
    	ArrayList warningsArrayList = new ArrayList();
    	if(expectedFunction.containsKey("warnings")) {
    		warningsArrayList = (ArrayList) expectedFunction.get("warnings");
    	}
    	Vector<Warning> warnings = actualFunction.getWarnings();
    	
    	testWarnings(warningsArrayList,warnings);
    	
	}
	
	private void testWarnings(ArrayList expectedWarnings, Vector<Warning> actualWarnings) {

    	assertEquals("Function does not have expected number of warnings.",expectedWarnings.size(),actualWarnings.size());
    	
    	for(int j=0; j < actualWarnings.size(); j++) {
    		
    		Map expectedWarning = (Map) expectedWarnings.get(j);
    		Warning actualWarning = actualWarnings.get(j);
    		
    		testWarning(expectedWarning,actualWarning);
    		
    	}
    	
	}
	
	private void testWarning(Map expectedWarning,Warning actualWarning) {
		assertEquals("Warning not found on correct line number.",Integer.parseInt((String) expectedWarning.get("line number")),actualWarning.getLineNumber());
		// TODO: we need to test on the variable name (or equivalent) and the type of warning.
	}
	
}
