package org.chaupal.jp2p.ui.property;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class Jp2pComponentPropertyDescriptor extends PropertyDescriptor {

	public Jp2pComponentPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		super.setLabelProvider( new LabelProvider());
	}	
}