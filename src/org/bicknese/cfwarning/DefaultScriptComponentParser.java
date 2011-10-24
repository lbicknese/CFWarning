package org.bicknese.cfwarning;

import java.util.Hashtable;
import java.util.Vector;

public class DefaultScriptComponentParser extends AbstractParser implements IComponentParser {

	private int commentCount = 0;
	private String commentType = "";
	private int commentLineNumber = 0;
	private int depth = 0;
	private int nest = 0;
	private int parenCount = 0;
	
	public DefaultScriptComponentParser(Vector<Function> functions) {
		super(functions);
	}
	
	public String parse(Tokens tokens) {
		return parseScriptComponent(tokens);
	}
	
	private String parseScriptComponent(Tokens tokens) {
		
		String currentToken = tokens.getNextToken();
		
		// keep parsing the file until we find the end of the component
		while(!EOF(currentToken) && !(compare(currentToken,"}") == 0)) {

			currentToken = tokens.getNextToken();
			
			if(!EOF(currentToken) && inComment(tokens)) {
				
				if(isComment(tokens)) commentCount++;
				
				if(isEndComment(tokens)) commentCount--;
				
			} else if(!EOF(currentToken) && isFunctionBeginScript(currentToken)) {
				
				currentToken = parseScriptFunction(tokens);
				
			}
			
		}
		
		return currentToken;
	}

	private String parseScriptFunction(Tokens tokens) {

		String currentToken = parseFunctionAttributes(tokens,tokens.getCurrentLineNumber(),tokens.getCurrentOffset());
		
		while(!EOF(currentToken) && depth == 0) {
			if(compare(currentToken,"{") == 0)
				depth++;
			currentToken = tokens.getNextToken();
		}
		
		while(!EOF(currentToken) && depth > 0) {
		
			if(isEndComment(tokens) && commentCount > 0) commentCount--;
			
			if(!EOF(currentToken) && inComment(tokens)) {

				if(isComment(tokens)) commentCount++;
				
			} else if(!EOF(currentToken)) {
				
				//currentToken = tokens.getNextToken();
				Function currentFunction = functions.lastElement();
				
				if(compare(currentToken,"{") == 0) {
					depth++;
					if(compare(tokens.getPreviousToken(), "=") == 0) {
						nest++;
					}
				} else if(compare(currentToken,"}") == 0) {
					depth--;
					if(nest > 0) {
						nest--;
					}
				} else if (compare(currentToken,"(") == 0 && compare(tokens.getPreviousToken(), "for") != 0) {
					parenCount++;
				} else if (compare(currentToken, ")") == 0 && parenCount > 0) {
					parenCount--;
				} else if(compare(currentToken,"=") == 0) {					
					
					String previousToken = tokens.getPreviousToken();
					String anotherToken = tokens.getToken(2);
					
					if(compare(previousToken, "<") == 0 || compare(previousToken, ">") == 0 || compare(previousToken, "!") == 0 || parenCount > 0 || nest > 0) {
						// do nothing
					} else if(compare(anotherToken,"var") == 0) {
						functions.lastElement().addLocalVar(previousToken, tokens.getCurrentLineNumber());
					} else {
						
						if(compare(previousToken, "]") == 0) {
							// TODO: go back until i find the variable
							int varLocation = parseStructVariable(tokens, 1);
							previousToken = tokens.getToken(varLocation);
							anotherToken = tokens.getToken(varLocation+1);
						}
						
						if(compare(previousToken, "action") != 0 && compare(anotherToken, "transaction") != 0) {
							handleVariable(previousToken,tokens);
						}
					}
					
					
				} else if (compare(currentToken,"writedump") == 0 || compare(currentToken,"abort") == 0) {
					Warning currentWarning = new Warning(tokens.getCurrentLineNumber(),tokens.getCurrentOffset(),"The method "+currentToken+" has been found in the code.","Debug");
					currentFunction.addWarning(currentWarning);
				}
			}

			currentToken = tokens.getNextToken();

		}
		//System.out.println("function end:"+tokens.getCurrentLineNumber());
		return tokens.getNextToken();
	}

	private String parseFunctionAttributes(Tokens tokens, int currentLineNumber, int currentOffset) {
		
		String returnType = tokens.getToken(1);
		String access = tokens.getToken(2);
		String functionName = tokens.getNextToken();
		
		Hashtable<String,String> attributes = new Hashtable<String,String>();
		
		attributes.put("name", functionName);
		attributes.put("access", access);
		attributes.put("returntype", returnType);
		
		addFunction(attributes,currentLineNumber,currentOffset);
		
		return tokens.getNextToken();
	}

	private boolean isFunctionBeginScript(String currentToken) {
		
		
		if (compare(currentToken,"function") == 0) {
			return true;
		}
		
		return false;
		
	}

	private boolean isComment(Tokens tokens) {
		String currentToken = tokens.getCurrentToken();
		String nextToken = tokens.lookAhead(1);
		
		if(compare(currentToken,"/") == 0 && compare(nextToken,"*") == 0) {
			commentType = "multi";
			return true;
		} else if(compare(currentToken,"/") == 0 && compare(nextToken,"/") == 0 && commentCount == 0) {
			commentType = "single";
			commentLineNumber = tokens.getCurrentLineNumber();
			return true;
		}
		
		return false;
		
	}

	private boolean isEndComment(Tokens tokens) {

		String currentToken = tokens.getCurrentToken();
		String nextToken = tokens.lookAhead(1);
		
		if(commentType == "multi" && compare(currentToken,"*") == 0 && compare(nextToken,"/") == 0) {
			return true;
		} else if(commentType == "single" && tokens.getCurrentLineNumber() != commentLineNumber) {
			return true;
		}
		
		return false;
	}

	private boolean inComment(Tokens tokens) {
		return commentCount > 0 || isComment(tokens);
	}

}
