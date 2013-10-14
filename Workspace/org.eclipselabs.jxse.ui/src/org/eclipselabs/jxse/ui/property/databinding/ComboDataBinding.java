package org.eclipselabs.jxse.ui.property.databinding;

import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.properties.ManagedPropertyEvent;
import net.osgi.jxse.utils.Utils;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;

public class ComboDataBinding<T extends Enum<T>, U extends Enum<U>> extends AbstractManagedPropertySourceDatabinding<T, U>{

	private Combo combo;
	
	@SuppressWarnings("unchecked")
	public ComboDataBinding(ManagedProperty<T, Enum<U>> managedProperty, Combo combo) {
		super((ManagedProperty<T, U>) managedProperty);
		combo.addSelectionListener(this);
		combo.setData(this);
		combo.select( managedProperty.getValue().ordinal());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ComboDataBinding( T id, IJxsePropertySource<T, ?> source, Combo combo) {
		this(( ManagedProperty ) source.getManagedProperty(id), combo);
	}

	@Override
	public void notifyValueChanged(ManagedPropertyEvent<T, U> event) {
		this.combo.select( event.getValue().ordinal() );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void widgetSelected(SelectionEvent e) {
		Combo combo = ( Combo )e.widget;
		String str = combo.getText();
		if( Utils.isNull(str)){
			super.getSource().setValue(null);
		}else{
			U value= (U) Enum.valueOf( super.getSource().getValue().getClass(), str.toUpperCase() );
			super.getSource().setValue( value );
		}
	}

	@Override
	public void dispose() {
		this.combo.removeSelectionListener(this);
	}
}
