package org.eclipselabs.jxse.ui.property.databinding;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import net.osgi.jxse.properties.IJxseValidator;
import net.osgi.jxse.properties.IManagedPropertyListener;
import net.osgi.jxse.properties.ManagedProperty;

public abstract class AbstractManagedPropertySourceDatabinding<T,U> implements
		IManagedPropertyListener<T, U>, SelectionListener, IJxseDatabinding<T, U> {

	private ManagedProperty<T,U> source;
	
	public AbstractManagedPropertySourceDatabinding( ManagedProperty<T,U> source) {
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see org.eclipselabs.jxse.ui.property.databinding.IJseDatabinding#setValidator(net.osgi.jxse.properties.IJxseValidator)
	 */
	@Override
	public void setValidator( IJxseValidator<T,U> validator ){
		this.source.setValidator(validator);
	}
	
	protected ManagedProperty<T, U> getSource() {
		return source;
	}

	/* (non-Javadoc)
	 * @see org.eclipselabs.jxse.ui.property.databinding.IJseDatabinding#dispose()
	 */
	@Override
	public abstract void dispose();

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
