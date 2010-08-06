package org.bicknese.cfwarning;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

public class Function implements IWorkbenchAdapter,IAdaptable,IStructuredSelection, IClick {
	
	String functionName;
	Hashtable<String,String> attributes;
	int lineNumber;
	int offset;
	Vector<Warning> warnings;
	Hashtable<String,Integer> localVars;
	
	public Function(String name,int lineNumber,int offset) {
		functionName = name;
		this.lineNumber = lineNumber;
		this.offset = offset;
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
	public String getLabel(Object o) {
		return lineNumber+": Function = "+functionName;
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class)
			return this;
		if (adapter == IPropertySource.class)
			return new FunctionProperties(this);
		return null;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public int getRange() {
		return 0;
	}

	@Override
	public Object getFirstElement() {
		return this;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Iterator<Object> iterator() {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public List<Object> toList() {
		return null;
	}

}
