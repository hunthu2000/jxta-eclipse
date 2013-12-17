package org.eclipselabs.jxse.ui.property;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class JxseComponentPropertyDescriptor extends PropertyDescriptor {

	public JxseComponentPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		super.setLabelProvider( new LabelProvider());
	}	
}