package org.bicknese.cfwarning;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class GeneralInfo implements  IWorkbenchAdapter,IAdaptable {
	
	private int numberOfFunctions;
	private int numberOfWarnings;
	
	public GeneralInfo(Function[] functions) {
		
		numberOfFunctions = functions.length;
		numberOfWarnings = 0;
		
		for(int i = 0; i < functions.length; i++) {
			Function function = functions[i];
			numberOfWarnings += function.getChildren(function).length;
		}
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getChildren(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel(Object o) {
		return "CFWarnging parsed "+numberOfFunctions+" functions and found "+numberOfWarnings+" warnings.";
	}

	@Override
	public Object getParent(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

}
