package org.bicknese.cfwarning;

import java.util.Vector;

public class Tokens {

	private Vector<StringBuilder> tokens;
	private int currentFilePosition;
	private int currentLineNumber;
	private String fileText;
	
	public Tokens(String text) {
		tokens = new Vector<StringBuilder>();
		currentFilePosition = 0;
		currentLineNumber = 1;
		fileText = text;
	}
	
	public int getCurrentLineNumber() {
		return currentLineNumber;
	}
	
	public String getNextToken() {
		
		if (isEOF())
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
		
		tokens.add(new StringBuilder(""));
		
		// eat the white space
		while(!isEOF() && isWhitespace(fileText.charAt(currentFilePosition))) {
			if (isEndLine(fileText.charAt(currentFilePosition))) {
				currentLineNumber++;
			}
			currentFilePosition++;
		}
		
		while(!isEOF()) {
			
			Character c = fileText.charAt(currentFilePosition);
			
			// Stop creating token at white space
			if(isWhitespace(c)) {
				break;
			}
			// don't add token separator to current token
			else if (isTokenSeparator(c) && getCurrentToken().length() > 0) {
				break;
			}
			
			// append to the current token
			appendToCurrentToken(c);

			currentFilePosition++;
			
			// if the current token is a separator don't append any more
			if (isTokenSeparator(getCurrentToken().charAt(0))) {
				break;
			}
			
			if (isTokenSeparatorIncluded(c) && getCurrentToken().length() > 1) {
				break;
			}
			
		}
		
		if (currentFilePosition == fileText.length()) 
			return null;
		
		return getCurrentToken();
		
	}
	
	private void appendToCurrentToken(char charAt) {
		tokens.get(tokens.size()-1).append(charAt);
	}

}
