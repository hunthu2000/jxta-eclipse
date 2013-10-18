package org.eclipselabs.jxse.ui.property.databinding;

import net.osgi.jxse.properties.IJxseWritePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.properties.ManagedPropertyEvent;
import net.osgi.jxse.utils.Utils;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

public class StringDataBinding<T extends Enum<T>> extends AbstractManagedPropertySourceDatabinding<T, String>
	implements ModifyListener
{

	private Text text;
	
	public StringDataBinding(ManagedProperty<T, String> managedProperty, Text text) {
		super((ManagedProperty<T, String>) managedProperty);
		text.addModifyListener(this);
		text.setData(this);
		if( !Utils.isNull( managedProperty.getValue() ))
			text.setText((String) managedProperty.getValue());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public StringDataBinding( T id, IJxseWritePropertySource source, Text text) {
		this( source.getOrCreateManagedProperty(id, null, false), text);
		text.setData(this);
	}

	@Override
	public void notifyValueChanged(ManagedPropertyEvent<T, String> event) {
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

	@Override
	public void modifyText(ModifyEvent e) {
		Text text = ( Text )e.widget;
		super.getSource().setValue( text.getText() );
	}
}