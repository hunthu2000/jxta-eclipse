package org.chaupal.jp2p.ui.jxta.property.databinding;

import java.net.URI;

import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.ManagedProperty;
import net.jp2p.container.properties.ManagedPropertyEvent;

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
	public URIDataBinding( T id, IJp2pWritePropertySource source, Text text) {
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