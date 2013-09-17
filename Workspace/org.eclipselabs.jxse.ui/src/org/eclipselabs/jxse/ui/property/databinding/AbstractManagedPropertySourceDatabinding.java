package org.eclipselabs.jxse.ui.property.databinding;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import net.osgi.jxse.properties.IManagedPropertyListener;
import net.osgi.jxse.properties.ManagedProperty;

public abstract class AbstractManagedPropertySourceDatabinding<T extends Enum<T>,U> implements
		IManagedPropertyListener<T, U>, SelectionListener {

	private ManagedProperty<T,U> source;
	
	public AbstractManagedPropertySourceDatabinding( ManagedProperty<T,U> source) {
		this.source = source;
	}

	
	protected ManagedProperty<T, U> getSource() {
		return source;
	}

	/**
	 * Dispose the data binding
	 */
	public abstract void dispose();

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
