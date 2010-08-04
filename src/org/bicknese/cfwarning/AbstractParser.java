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
	
	protected void addFunction(Hashtable<String,String> attributes,int lineNumber) {
		
		functions.add(new Function(attributes.get("name"),lineNumber));
		
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

}
