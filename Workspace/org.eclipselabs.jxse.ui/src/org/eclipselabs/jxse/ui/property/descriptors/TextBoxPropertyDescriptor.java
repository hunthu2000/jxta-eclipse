package org.eclipselabs.jxse.ui.property.descriptors;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipselabs.jxse.ui.celleditors.AbstractControlCellEditor;
import org.eclipselabs.jxse.ui.celleditors.TextBoxCellEditor;
import org.eclipselabs.jxse.ui.provider.ControlLabelProvider;

public class TextBoxPropertyDescriptor extends AbstractControlPropertyDescriptor<String> {

	private String value;
	
	private TextBoxCellEditor editor;
	
	public TextBoxPropertyDescriptor(Object id, String displayName )
	{
		super(id, displayName );
	}

	
	@Override
	protected AbstractControlCellEditor onCreatePropertyEditor(Composite parent) {
		this.editor = new TextBoxCellEditor(parent, value, SWT.NONE );
		editor.setEnabled( super.isEnabled());
		return editor;
	}

	@Override
	public ILabelProvider getLabelProvider()
	{
		return new ControlLabelProvider( this );
	}
}
