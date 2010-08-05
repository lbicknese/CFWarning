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
		
		while(!EOF(currentToken) && !(compare(currentToken,"cffunction") == 0 && compare(tokens.getPreviousToken(),"/") == 0)) {
		
			currentToken = tokens.getNextToken();
			
			if(!EOF(currentToken) && inComment(tokens)) {
				
				if(isComment(tokens)) commentCount++;
				
				if(isEndComment(tokens)) commentCount--;
				
			} else if(!EOF(currentToken) && compare(currentToken,"<") == 0 && compare(tokens.lookAhead(1),"/") != 0) {
				
				currentToken = tokens.getNextToken();
				Function currentFunction = functions.lastElement();
				
				if(compare(currentToken,"cfset") == 0) {
					
					currentToken = parseCfsetTag(tokens);
					
				} else if (compare(currentToken,"cfscript") == 0) {
					
					currentToken = parseCfscriptTag(tokens);
					
				} else if (tags.isReturnTag(currentToken)) {
					
					String tagName = currentToken;
					
					Hashtable <String,String> attributes = parseAttributes(tokens);
					String name = tags.returnValue(tagName, attributes);
					
					if(name.compareTo("") != 0) {
						handleVariable(name,tokens);
					}
					
				} else if (compare(currentToken,"cfdump") == 0 || compare(currentToken,"cfabort") == 0) {
					Warning currentWarning = new Warning(tokens.getCurrentLineNumber(),"The tag "+currentToken+" has been found in the code.","Debug");
					currentFunction.addWarning(currentWarning);
				}
			}
		}
		
		return currentToken;
	}
	
	private String parseCfsetTag(Tokens tokens) {
		
		String currentToken = tokens.getNextToken();
		Function currentFunction = functions.lastElement();
		
		if(compare(currentToken,"var") == 0) {
			
			currentToken = removeQuotes(tokens.getNextToken());
			functions.lastElement().addLocalVar(currentToken, tokens.getCurrentLineNumber());
			
		} else {
			
			String previousToken = removeQuotes(tokens.getCurrentToken());
			currentToken = tokens.getNextToken();
			
			if(compare(currentToken,"=") == 0) {
				
				handleVariable(previousToken,tokens);
				
			}
		}
		
		return tokens.getCurrentToken();
		
	}
	
	private String parseCfscriptTag(Tokens tokens) {
		
		int parenCount = 0;
		int currentLineNumber = tokens.getCurrentLineNumber();
		Boolean isComment = false;
		String currentToken = tokens.getNextToken();
		Function currentFunction = functions.lastElement();
		
		// get to the end of the cfscript tag
		while(!EOF(currentToken) && compare(currentToken,">") != 0) {
			currentToken = tokens.getNextToken();
		}
		
		currentToken = tokens.getNextToken();
		
		while(!EOF(currentToken) && !(compare(currentToken,"cfscript") == 0 && compare(tokens.getPreviousToken(),"/") == 0)) {
		
			if(compare(currentToken,"(") == 0)
				parenCount++;
				
			if(compare(currentToken,")") == 0)
				parenCount--;
			
			// handle comments
			if(compare(currentToken,"/") == 0 && compare(tokens.lookAhead(1),"/") == 0) {
				isComment = true;
				currentLineNumber = tokens.getCurrentLineNumber();
			}
			if(tokens.getCurrentLineNumber() != currentLineNumber) {
				isComment = false;
			}	
			
			if(compare(currentToken,"=") == 0 && parenCount == 0 && !isComment) {
				
				String var = tokens.getToken(2);
				String variable = tokens.getPreviousToken();
				
				if(compare(var,"var") == 0) {
					functions.lastElement().addLocalVar(variable, tokens.getCurrentLineNumber());
				} else {
					handleVariable(variable,tokens);
				}
				
			}
			
			currentToken = tokens.getNextToken();
		
		}
		
		return currentToken;
		
	}
	
	private String parseFunctionAttributes(Tokens tokens, int tagLineNumber) {
		
		Hashtable<String,String> attributes = parseAttributes(tokens);
		addFunction(attributes,tagLineNumber);
		return tokens.getCurrentToken();
		
	}
	
	private Hashtable<String,String> parseAttributes(Tokens tokens) {
		
		Hashtable<String,String> attributes = new Hashtable<String,String>();

		String currentToken = tokens.getNextToken();
		String attribute = currentToken;
		String value = "";
		String equalToken = "";
		
		while(!EOF(currentToken) && compare(currentToken,">") != 0) {
			
			if(!EOF(currentToken)) {
				// eat the equals sign
				currentToken = tokens.getNextToken();
				equalToken = currentToken;
			}
			
			if(!EOF(currentToken) && compare(equalToken,"=") == 0) {
				// get the value of the attribute
				value = getAttributeValue(tokens);
				currentToken = tokens.getCurrentToken();
			}
			
			if(!EOF(currentToken) && compare(equalToken,"=") == 0) {
				// the value two tokens ago is the name of the attribute
				// the current token is the value of the attribute
				attributes.put(attribute.toLowerCase(), removeQuotes(value));
			}

			currentToken = tokens.getNextToken();
			attribute = currentToken;
			
		}
		
		return attributes;
		
	}

	private String getAttributeValue(Tokens tokens) {
		// this function is designed to get the value of an attribute.
		// it takes care of instances where there might be spaces in the value.
		String currentToken = tokens.getNextToken();
		String value = currentToken;
		Character c = currentToken.charAt(0);
		
		if(!EOF(currentToken) && (c == '\'' || c == '\"')) {
			
			while(!EOF(currentToken) && value.charAt(value.length()-1) != c) {
				currentToken = tokens.getNextToken();
				value += currentToken;
			}
			
		}
		
		return value;
	}
	
	private void handleVariable(String variable, Tokens tokens) {
		
		Function currentFunction = functions.lastElement();
		
		if(isScoped(variable)) {
			if(compare(getScope(variable),"local") == 0) {
				functions.lastElement().addLocalVar(variable, tokens.getCurrentLineNumber());
			} 
		} else {
			if(!isLocalScoped(variable)) {
				Warning currentWarning = new Warning(tokens.getCurrentLineNumber(),"The variable "+variable+" is not scoped.","Scope");
				currentFunction.addWarning(currentWarning);
			}
		}
		
		return;
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

}
