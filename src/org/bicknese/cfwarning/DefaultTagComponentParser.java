package org.bicknese.cfwarning;

import java.util.Hashtable;
import java.util.Vector;

public class DefaultTagComponentParser extends AbstractParser {
	
	private TagLibrary tags = TagLibrary.getInstance();
	private int commentCount = 0;
	
	public DefaultTagComponentParser(Vector<Function> functions) {
		super(functions);
	}
	
	public String parse(Tokens tokens) {
		return parseTagComponent(tokens);
	}

	private String parseTagComponent(Tokens tokens) {
		
		String currentToken = tokens.getNextToken();
		
		// keep parsing the file until we find the end of the component
		while(!EOF(currentToken) && !(compare(currentToken,"cfcomponent") == 0 && compare(tokens.getPreviousToken(),"/") == 0)) {

			currentToken = tokens.getNextToken();
			
			if(!EOF(currentToken) && inComment(tokens)) {
				
				if(isComment(tokens)) commentCount++;
				
				if(isEndComment(tokens)) commentCount--;
				
			} else if(!EOF(currentToken) && isFunctionBeginTag(currentToken,tokens.getPreviousToken())) {
				
				currentToken = parseTagFunction(tokens);
				
			}
			
		}
		
		return currentToken;
	}
	
	private String parseTagFunction(Tokens tokens) {
		
		String currentToken = parseFunctionAttributes(tokens,tokens.getCurrentLineNumber());
		
		if(!EOF(currentToken)) {
			
			while(!EOF(currentToken) && !(compare(currentToken,"cffunction") == 0 && compare(tokens.getPreviousToken(),"/") == 0)) {
			
				currentToken = tokens.getNextToken();
				
				if(!EOF(currentToken) && inComment(tokens)) {
					
					if(isComment(tokens)) commentCount++;
					
					if(isEndComment(tokens)) commentCount--;
					
				} else if(!EOF(currentToken) && compare(currentToken,"<") == 0 && compare(tokens.lookAhead(1),"/") != 0) {
					
					currentToken = tokens.getNextToken();
					Function currentFunction = functions.lastElement();
					
					if(compare(currentToken,"cfset") == 0) {
						
						currentToken = tokens.getNextToken();
						
						if(compare(currentToken,"var") == 0) {
							
							currentToken = removeQuotes(tokens.getNextToken());
							functions.lastElement().addLocalVar(currentToken, tokens.getCurrentLineNumber());
							
						} else {
							
							// TODO: check if the variable is scoped.
							String previousToken = removeQuotes(tokens.getCurrentToken());
							currentToken = tokens.getNextToken();
							
							if(compare(currentToken,"=") == 0) {
								
								if(isScoped(previousToken)) {
									// only add to local vars if it is locally scoped
									if(compare(getScope(previousToken),"local") == 0) {
										functions.lastElement().addLocalVar(previousToken, tokens.getCurrentLineNumber());
									} 
								} else {
									if(!isLocalScoped(previousToken)) {
										Warning currentWarning = new Warning(tokens.getCurrentLineNumber(),"The variable "+previousToken+" is not scoped.","Scope");
										currentFunction.addWarning(currentWarning);
									}
								}
								
							}
						}
						
					} else if (compare(currentToken,"cfscript") == 0) {
						
						int parenCount = 0;
						currentToken = tokens.getNextToken();
						
						while(!EOF(currentToken) && compare(currentToken,">") != 0) {
							currentToken = tokens.getNextToken();
						}
						
						currentToken = tokens.getNextToken();
						
						while(!EOF(currentToken) && !(compare(currentToken,"cfscript") == 0 && compare(tokens.getPreviousToken(),"/") == 0)) {
						
							if(compare(currentToken,"(") == 0)
								parenCount++;
								
							if(compare(currentToken,")") == 0)
								parenCount--;
							
							if(compare(currentToken,"=") == 0 && parenCount == 0) {
								
								String var = tokens.getToken(2);
								String variable = tokens.getPreviousToken();
								
								if(compare(var,"var") == 0) {
									functions.lastElement().addLocalVar(variable, tokens.getCurrentLineNumber());
								} else if (isScoped(variable) && compare(getScope(variable),"local") == 0) {
									functions.lastElement().addLocalVar(variable, tokens.getCurrentLineNumber());
								} else if (!isScoped(variable) && !isLocalScoped(variable)) {
									Warning currentWarning = new Warning(tokens.getCurrentLineNumber(),"The variable "+variable+" is not scoped.","Scope");
									currentFunction.addWarning(currentWarning);
								}
								
							}
							
							currentToken = tokens.getNextToken();
						
						}
						
					} else if (tags.isReturnTag(currentToken)) {
						// TODO: I think there is some more work here for tags like cfinvoke
						String tagName = currentToken;
						
						Hashtable <String,String> attributes = parseAttributes(tokens);
						String name = tags.returnValue(tagName, attributes);
						
						if(compare(getScope(name),"local") != 0 && name.compareTo("") != 0) {
							
							if(!isLocalScoped(name)) {
								Warning currentWarning = new Warning(tokens.getCurrentLineNumber(),"The variable "+name+" is not scoped.","Scope");
								currentFunction.addWarning(currentWarning);
							}
							
						}
					}			
				}
			}
		}
		
		return currentToken;
	}
	
	private boolean inComment(Tokens tokens) {
		return commentCount > 0 || isComment(tokens);
	}

	private boolean isEndComment(Tokens tokens) {
		String currentToken = tokens.getCurrentToken();
		String tail = tokens.lookAhead(3);
		return compare(currentToken,"-") == 0 && compare(tail,"-->") == 0;
	}

	private boolean isComment(Tokens tokens) {
		String currentToken = tokens.getCurrentToken();
		String tail = tokens.lookAhead(4);
		return compare(currentToken,"<") == 0 && compare(tail,"!---") == 0;
	}

	private String parseFunctionAttributes(Tokens tokens, int tagLineNumber) {
		
		Hashtable<String,String> attributes = parseAttributes(tokens);
		addFunction(attributes,tagLineNumber);
		return tokens.getCurrentToken();
		
	}
	
	private Hashtable<String,String> parseAttributes(Tokens tokens) {
		
		String currentToken = tokens.getCurrentToken();
		Hashtable<String,String> attributes = new Hashtable<String,String>();

		currentToken = tokens.getNextToken();
		
		while(!EOF(currentToken) && compare(currentToken,">") != 0) {
			
			if(!EOF(currentToken)) {
				// eat the equals sign
				currentToken = tokens.getNextToken();
			}
			
			if(!EOF(currentToken)) {
				// get the value of the attribute
				currentToken = tokens.getNextToken();
			}
			
			if(!EOF(currentToken) && compare(tokens.getPreviousToken(),"=") == 0) {
				// the value two tokens ago is the name of the attribute
				// the current token is the value of the attribute
				attributes.put(tokens.getToken(2).toLowerCase(), removeQuotes(currentToken));
			}

			currentToken = tokens.getNextToken();
			
		}
		
		return attributes;
		
	}

	private Boolean isFunctionBeginTag(String token, String previousToken) {
		
		if (compare(previousToken,"/") == 0) {
			return false;
		}
		
		if (compare(token,"cffunction") == 0) {
			return true;
		}
		
		return false;
		
	}
	
	private Boolean isLocalScoped(String variable) {
		Function currentFunction = functions.lastElement();
		
		if(currentFunction.isScoped(variable))
			return true;
		
		if(currentFunction.isScoped("local."+variable))
			return true;
		
		String[] parts = splitVar(variable);
		String part = "";
		for(int i = 0; i < parts.length; i++) {
			
			if(i==0)
				part = parts[i];
			else
				part += "."+parts[i];
			
			
			if(currentFunction.isScoped(parts[i]))
				return true;
			if(currentFunction.isScoped("local."+parts[i]))
				return true;
		}
		
		return false;
	}
	
	private Boolean isScoped(String variable) {
		String[] parts = splitVar(variable);
		return scopes.contains(parts[0].toLowerCase());
	}
	
	private String getScope(String variable) {
		String[] parts = splitVar(variable);
		return parts[0];
	}
	
	private String[] splitVar(String variable) {
		return variable.split("[\\.\\[]");
	}
}
