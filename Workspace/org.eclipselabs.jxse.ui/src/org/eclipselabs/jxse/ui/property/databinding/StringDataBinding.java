package org.eclipselabs.jxse.ui.property.databinding;

import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.properties.ManagedPropertyEvent;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

public class StringDataBinding<T extends Enum<T>> extends AbstractManagedPropertySourceDatabinding<T, Object>{

	private Text text;
	
	@SuppressWarnings("unchecked")
	public StringDataBinding(ManagedProperty<T, ?> managedProperty, Text text) {
		super((ManagedProperty<T, Object>) managedProperty);
		text.addSelectionListener(this);
		text.setData(this);
	}

	public StringDataBinding( T id, IJxsePropertySource<T, ?> source, Text text) {
		this( source.getManagedProperty(id), text);
		text.setData(this);
	}

	@Override
	public void notifyValueChanged(ManagedPropertyEvent<T, Object> event) {
		this.text.setText(( String )event.getValue());
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Text text = ( Text )e.widget;
		super.getSource().setValue( text.getText() );
	}

	@Override
	public void dispose() {
		this.text.removeSelectionListener(this);
	}
}
