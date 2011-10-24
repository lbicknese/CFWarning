package org.bicknese.cfwarning;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class DefaultParser implements IFileParser {
	
	private Vector<Function> functions;
	
	public DefaultParser () {
		
		functions = new Vector<Function>();
		
	}
	
	@Override
	public Function[] parse(IFile file) {
		
		functions = new Vector<Function>();
		
		String fileText = getText(file);
		
		Tokens tokens = new Tokens(fileText);
		
		String currentToken = "";
		/*while(!EOF(currentToken)) {
			currentToken = tokens.getNextToken();
			System.out.println(currentToken);
		}*/
		
		// find the beginning of the component
		while(!EOF(currentToken) && currentToken.compareToIgnoreCase(ITokensConstants.BEGIN_COMPONENT_SCRIPT) != 0 && currentToken.compareToIgnoreCase(ITokensConstants.BEGIN_COMPONENT_TAG) != 0) {
			currentToken = tokens.getNextToken();
		}
		
		if(!EOF(currentToken)) {
			if(currentToken.compareToIgnoreCase(ITokensConstants.BEGIN_COMPONENT_SCRIPT) == 0) {
				DefaultScriptComponentParser parser = new DefaultScriptComponentParser(functions);
				currentToken = parser.parse(tokens);
			} else if(currentToken.compareToIgnoreCase(ITokensConstants.BEGIN_COMPONENT_TAG) == 0) {
				DefaultTagComponentParser parser = new DefaultTagComponentParser(functions);
				currentToken = parser.parse(tokens);
			}
		}
		
		Function[] results = new Function[functions.size()];
		functions.copyInto(results);
		
		return results;
		
	}
	
	private Boolean EOF(String currentToken) {
		return currentToken == null;
	}

	private String getText(IFile file) {
		
		try {
			
			InputStream in = file.getContents();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			
			int read = in.read(buf);
			
			while (read > 0) {
				out.write(buf, 0, read);
				read = in.read(buf);
			}
			
			return out.toString();
			
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
		
	}

}
