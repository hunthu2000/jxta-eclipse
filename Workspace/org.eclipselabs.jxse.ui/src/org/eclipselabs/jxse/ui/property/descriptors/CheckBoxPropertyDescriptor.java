package org.eclipselabs.jxse.ui.property.descriptors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipselabs.jxse.ui.celleditors.CheckBoxCellEditor;
import org.eclipselabs.jxse.ui.provider.CheckBoxLabelProvider;

public class CheckBoxPropertyDescriptor extends PropertyDescriptor {

	static final CheckBoxLabelProvider LABEL_PROVIDER = new CheckBoxLabelProvider();

	public CheckBoxPropertyDescriptor(Object id, String displayName)
	{
		super(id, displayName);
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent)
	{
		return new CheckBoxCellEditor(parent);
	}

	@Override
	public ILabelProvider getLabelProvider()
	{
		return LABEL_PROVIDER;
	}
}
