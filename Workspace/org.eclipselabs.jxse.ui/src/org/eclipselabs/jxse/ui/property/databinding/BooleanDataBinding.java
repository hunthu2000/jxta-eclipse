package org.eclipselabs.jxse.ui.property.databinding;

import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.properties.ManagedPropertyEvent;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class BooleanDataBinding<T extends Enum<T>> extends AbstractManagedPropertySourceDatabinding<T, Boolean>{

	private Button button;
	
	public BooleanDataBinding(ManagedProperty<T, Boolean> source, Button button) {
		super(source);
		button.addSelectionListener(this);
	}

	@Override
	public void notifyValueChanged(ManagedPropertyEvent<T, Boolean> event) {
		this.button.setSelection( event.getValue());
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Button button = ( Button )e.widget;
		super.getSource().setValue( button.getSelection() );
	}

	@Override
	public void dispose() {
		this.button.removeSelectionListener(this);
	}

}
