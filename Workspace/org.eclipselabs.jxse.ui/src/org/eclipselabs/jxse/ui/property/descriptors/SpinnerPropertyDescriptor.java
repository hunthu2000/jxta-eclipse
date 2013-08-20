package org.eclipselabs.jxse.ui.property.descriptors;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipselabs.jxse.ui.celleditors.AbstractControlCellEditor;
import org.eclipselabs.jxse.ui.celleditors.SpinnerCellEditor;
import org.eclipselabs.jxse.ui.provider.ControlLabelProvider;

public class SpinnerPropertyDescriptor extends AbstractControlPropertyDescriptor<Integer> {

	private int minValue, maxValue;
	
	private SpinnerCellEditor editor;
	
	public SpinnerPropertyDescriptor(Object id, String displayName, int minValue, int maxValue)
	{
		super(id, displayName );
		this.maxValue = minValue;
		this.maxValue = maxValue;
	}

	public SpinnerPropertyDescriptor(Object id, String displayName, int maxValue)
	{
		this( id, displayName, 0, maxValue );
	}

	public SpinnerPropertyDescriptor(Object id, String displayName )
	{
		this( id, displayName, 0, 9999 );
	}
	
	@Override
	protected AbstractControlCellEditor onCreatePropertyEditor(Composite parent) {
		this.editor = new SpinnerCellEditor(parent, minValue, maxValue, SWT.NONE );
		editor.setEnabled( super.isEnabled());
		return editor;
	}

	@Override
	public ILabelProvider getLabelProvider()
	{
		return new ControlLabelProvider( this );
	}
}
