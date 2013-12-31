package org.chaupal.jp2p.ui.property.databinding;

import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.properties.ManagedPropertyEvent;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class BooleanDataBinding<T extends Object> extends AbstractManagedPropertySourceDatabinding<T, Boolean>{

	private Button button;
	
	public BooleanDataBinding(ManagedProperty<T, Boolean> source, Button button) {
		super(source);
		button.addSelectionListener(this);
		button.setSelection(source.getValue());
		button.setData(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BooleanDataBinding( T id, IJp2pPropertySource<T> source, Button button) {
		this((ManagedProperty)source.getManagedProperty(id), button);
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
