package org.bicknese.cfwarning;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class Warning implements IWorkbenchAdapter,IAdaptable,IStructuredSelection, IClick {
	
	// TODO: need to have a name or variable attribute. Also, may want to re-implement the warning into sub-classes.
	int lineNumber;
	String warningMessage;
	String warningType;
	int offset;
	
	public Warning(int lineNumber,int offset,String warningMessage,String warningType) {
		this.lineNumber = lineNumber;
		this.warningMessage = warningMessage;
		this.warningType = warningType;
		this.offset = offset;
	}
	
	public int getLineNumber() {return lineNumber;}
	
	public String getWarningMessage() {return warningMessage;}
	
	public String getWarningType() {return warningType;}

	@Override
	public String getLabel(Object o) {
		return lineNumber+": "+warningMessage;
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
	public String toString() {
		return getLabel(null);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public Object[] getChildren(Object o) {
		return null;
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
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	@Override
	public Object getFirstElement() {
		return this;
	}

	@Override
	public Iterator<Object> iterator() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public List<Object> toList() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
