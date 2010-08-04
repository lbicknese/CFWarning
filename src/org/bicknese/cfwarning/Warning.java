package org.bicknese.cfwarning;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class Warning implements IWorkbenchAdapter,IAdaptable {
	
	int lineNumber;
	String warningMessage;
	String warningType;
	
	public Warning(int lineNumber,String warningMessage,String warningType) {
		this.lineNumber = lineNumber;
		this.warningMessage = warningMessage;
		this.warningType = warningType;
	}
	
	public int getLineNumber() {return lineNumber;}
	
	public String getWarningMessage() {return warningMessage;}
	
	public String getWarningType() {return warningType;}

	@Override
	public Object[] getChildren(Object o) {
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	@Override
	public String getLabel(Object o) {
		return lineNumber+": "+warningMessage;
	}

	@Override
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	@Override
	public String toString() {
		return getLabel(null);
	}

}
