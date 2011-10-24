package org.bicknese.cfwarning;

import java.util.Hashtable;
import java.util.Vector;

public abstract class AbstractParser {

	protected Vector<String> scopes;
	protected Vector<Function> functions;
	
	public AbstractParser(Vector<Function> functions) {
		scopes = new Vector<String>();
		scopes.add("super");
		scopes.add("variables");
		scopes.add("this");
		scopes.add("cgi");
		scopes.add("form");
		scopes.add("url");
		scopes.add("application");
		scopes.add("arguments");
		scopes.add("cfcatch");
		scopes.add("cgi");
		scopes.add("client");
		scopes.add("cookie");
		scopes.add("request");
		scopes.add("server");
		scopes.add("session");
		scopes.add("local");
		scopes.add("attributes");
		scopes.add("thread");
		scopes.add("beans"); //this may not be the best thing...
		
		this.functions = functions;
	}
	
	protected void addFunction(Hashtable<String,String> attributes,int lineNumber,int offset) {
		
		functions.add(new Function(attributes.get("name"),lineNumber,offset));
		
	}

	protected void handleVariable(String variable, Tokens tokens) {
		
		Function currentFunction = functions.lastElement();
		
		if(isScoped(variable)) {
			if(compare(getScope(variable),"local") == 0) {
				functions.lastElement().addLocalVar(variable, tokens.getCurrentLineNumber());
			} 
		} else {
			if(!isLocalScoped(variable)) {
				Warning currentWarning = new Warning(tokens.getCurrentLineNumber(),tokens.getCurrentOffset(),"The variable "+variable+" is not scoped.","Scope");
				currentFunction.addWarning(currentWarning);
			}
		}
		
		return;
	}

	protected Boolean isLocalScoped(String variable) {
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
	
	protected Boolean isScoped(String variable) {
		String[] parts = splitVar(variable);
		return scopes.contains(parts[0].toLowerCase());
	}
	
	protected String getScope(String variable) {
		String[] parts = splitVar(variable);
		return parts[0];
	}

	protected String[] splitVar(String variable) {
		return variable.split("[\\.\\[]");
	}

	protected Boolean EOF(String currentToken) {
		return currentToken == null;
	}

	protected String removeQuotes(String valueToken) {
		
		if(valueToken.startsWith("\"") && valueToken.endsWith("\"")) {
			if(valueToken.length() <= 2)
				return "";
		
			return valueToken.substring(1, valueToken.length()-1);
		} else {
			return valueToken;
		}
	
	}
	
	protected int compare(String string1, String string2) {
		return string1.compareToIgnoreCase(string2);
	}
	
	protected int parseStructVariable(Tokens tokens, int start) {
		
		int back = start;
		
		// TODO: make sure i don't get an infinite loop...
		if(compare(tokens.getToken(back),"]") == 0) {
			while(compare(tokens.getToken(back),"[") != 0){
				back++;
			}
		}
		
		back++;
		
		if(compare(tokens.getToken(back),"]") == 0 ) {
			return parseStructVariable(tokens, back);
		}
		
		return back;
	}

}
