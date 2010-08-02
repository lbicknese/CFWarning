package org.bicknese.cfwarning;

import java.util.Vector;

public class Tokens {

	private Vector<StringBuilder> tokens;
	private int currentFilePosition;
	private int currentLineNumber;
	private String fileText;
	private Boolean isScript;
	
	public Tokens(String text) {
		tokens = new Vector<StringBuilder>();
		currentFilePosition = 0;
		currentLineNumber = 1;
		fileText = text;
		isScript = false;
	}
	
	public int getCurrentLineNumber() {
		return currentLineNumber;
	}
	
	public String getNextToken() {
		
		if (fileText.length() == currentFilePosition)
			return null;
		
		return nextToken();
	}
	
	public String getCurrentToken() {
		return getToken(0);
	}
	
	public String getPreviousToken() {	
		return getToken(1);
	}
	
	public String getToken(int location) {
		
		if (tokens.size() < location+1)
			return null;
			
		return tokenize(tokens.get(tokens.size()-location-1));
	}
	
	public String lookAhead(int numberOfCharacters) {
		
		StringBuilder text = new StringBuilder("");
		
		for (int i = 0; i < numberOfCharacters && fileText.length() > currentFilePosition+i; i++) {
			
			text.append(fileText.charAt(currentFilePosition+i));
				
		}
		
		return tokenize(text);
		
	}
	
	private String tokenize(StringBuilder token) {
		return token.toString().trim();
	}
	
	private Boolean isWhitespace(Character c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
	}
	
	private Boolean isTokenSeparator(Character c) {
		return c == '=' || c == '/' || c == '<' || c == '>' || c == '-' || c == '+' || c == '*' || c == '&' || c == '(' || c == ')' || c == ';' || c == ':';
	}
	
	private Boolean isTokenSeparatorIncluded(Character c) {
		return c == '"' && lookAhead(1).compareToIgnoreCase("\"") != 0;
	}
	
	private Boolean isEndLine(Character c) {
		return c == '\n';
	}
	
	private Boolean isEOF() {
		return !(currentFilePosition < fileText.length());
	}
	
	public String nextToken() {
		
		// TODO: need to parse out comments blocks and strings as single tokens...
		
		tokens.add(new StringBuilder(""));
		
		while(!isEOF() && isWhitespace(fileText.charAt(currentFilePosition))) {
			if (isEndLine(fileText.charAt(currentFilePosition))) {
				currentLineNumber++;
			}
			currentFilePosition++;
		}
		
		while(!isEOF() && !isWhitespace(fileText.charAt(currentFilePosition))) {
			
			// don't add token separator to current token
			if (isTokenSeparator(fileText.charAt(currentFilePosition)) && getCurrentToken().length() > 0) {
				break;
			}
			
			// append to the current token
			appendToCurrentToken(fileText.charAt(currentFilePosition));

			currentFilePosition++;
			
			// if the current token is a separator don't append any more
			if (isTokenSeparator(getCurrentToken().charAt(0))) {
				break;
			}
			
			if (isTokenSeparatorIncluded(fileText.charAt(currentFilePosition-1)) && getCurrentToken().length() > 1) {
				break;
			}
			
		}
		
		// toggle on and off script parsing
		if (isScript(getCurrentToken()))
			isScript = !isScript;
		
		if (currentFilePosition == fileText.length()) 
			return null;
		
		return getCurrentToken();
		
	}
	
	private Boolean isScript(String token) {
		return token.compareToIgnoreCase("component") == 0 || token.compareToIgnoreCase("cfscript") == 0;
	}
	
	public Boolean getIsScript() {
		return isScript;
	}
	
	private void appendToCurrentToken(char charAt) {
		tokens.get(tokens.size()-1).append(charAt);
	}

}
