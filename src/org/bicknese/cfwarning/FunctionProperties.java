package org.bicknese.cfwarning;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class FunctionProperties implements IPropertySource {
	protected Function element;

	protected static final String PROPERTY_FUNCTION_NAME = "lineno"; //$NON-NLS-1$
	protected static final String PROPERTY_LINE_NO = "start"; //$NON-NLS-1$

	public FunctionProperties(Function element) {
		super();
		this.element = element;
	}
	
	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// Create the property vector.
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[2];
	
		// Add each property supported.
		PropertyDescriptor descriptor;
	
		descriptor = new PropertyDescriptor(PROPERTY_FUNCTION_NAME, "Function Name");
		propertyDescriptors[0] = descriptor;
		descriptor = new PropertyDescriptor(PROPERTY_LINE_NO, "Line Number");
		propertyDescriptors[1] = descriptor;
	
		// Return it.
		return propertyDescriptors;
	}

	@Override
	public Object getPropertyValue(Object name) {
		if (name.equals(PROPERTY_FUNCTION_NAME))
			return element.getFunctionName();
		
		if (name.equals(PROPERTY_LINE_NO))
			return new Integer(element.getLineNumber());
		
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
	}

}
