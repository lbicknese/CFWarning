package org.bicknese.cfwarning;

import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

public class Function implements IWorkbenchAdapter,IAdaptable {
	
	String functionName;
	Hashtable<String,String> attributes;
	int lineNumber;
	Vector<Warning> warnings;
	Hashtable<String,Integer> localVars;
	
	public Function(String name,int lineNumber) {
		functionName = name;
		this.lineNumber = lineNumber;
		attributes = new Hashtable<String,String>();
		warnings = new Vector<Warning>();
		localVars = new Hashtable<String,Integer>();
	}
	
	public String getFunctionName() { return functionName; }
	
	public int getLineNumber() { return lineNumber; }
	
	public String toString() {
		return "{name="+functionName+", line number="+lineNumber+", localVars="+localVars+" warnings="+warnings+"}";
	}
	
	public void setAttributes(Hashtable<String,String> attributes) {
		this.attributes = attributes;
	}
	
	public void addWarning(Warning warning) {
		warnings.add(warning);
	}
	
	public void addLocalVar(String varName, int varLineNumber) {
		localVars.put(varName.toLowerCase(), varLineNumber);
	}
	
	public Boolean isScoped(String varName) {
		//TODO: this needs to be updated to check on complex objects
		return localVars.containsKey(varName.toLowerCase());
	}
	
	public Vector<Warning> getWarnings() {return warnings;}

	@Override
	public Object[] getChildren(Object o) {
		Object[] warningsArray = new Object[warnings.size()];
		warnings.copyInto(warningsArray);
		return warningsArray;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel(Object o) {
		return lineNumber+": Function = "+functionName;
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class)
			return this;
		if (adapter == IPropertySource.class)
			return new FunctionProperties(this);
		return null;
	}

}
