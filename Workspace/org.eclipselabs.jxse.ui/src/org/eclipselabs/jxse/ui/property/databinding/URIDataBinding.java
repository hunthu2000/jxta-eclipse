package org.eclipselabs.jxse.ui.property.databinding;

import java.net.URI;

import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.properties.ManagedPropertyEvent;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

public class URIDataBinding<T extends Enum<T>> extends AbstractManagedPropertySourceDatabinding<T, URI>{

	private Text text;
	
	public URIDataBinding(ManagedProperty<T, URI> managedProperty, Text text) {
		super((ManagedProperty<T, URI>) managedProperty);
		text.addSelectionListener(this);
		text.setData(this);
		if(( managedProperty.getValue() != null ) && ( managedProperty.getValue().getPath() != null ))
			text.setText( managedProperty.getValue().getPath());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public URIDataBinding( T id, IJxseWritePropertySource source, Text text) {
		this( source.getOrCreateManagedProperty(id, null, false), text);
		text.setData(this);
	}

	@Override
	public void notifyValueChanged(ManagedPropertyEvent<T, URI> event) {
		this.text.setText( event.getValue().getPath());
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Text text = ( Text )e.widget;
		super.getSource().setValue( URI.create( text.getText() ));
	}

	@Override
	public void dispose() {
		this.text.removeSelectionListener(this);
	}
}
