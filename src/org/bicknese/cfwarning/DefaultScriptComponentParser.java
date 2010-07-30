package org.bicknese.cfwarning;

import java.util.Vector;

public class DefaultScriptComponentParser extends AbstractParser {

	public DefaultScriptComponentParser(Vector<Function> functions) {
		super(functions);
	}
	
	public String parse(Tokens tokens) {
		return null;
	}

	private Boolean isFunctionBeginScript(String token) {
		
		if(token.compareToIgnoreCase(ITokensConstants.BEGIN_FUNCTION_SCRIPT) == 0) {
			return true;
		}
		
		return false;
		
	}
	
}
