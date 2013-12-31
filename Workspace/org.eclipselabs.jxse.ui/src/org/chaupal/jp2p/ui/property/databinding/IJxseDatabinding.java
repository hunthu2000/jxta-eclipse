package org.chaupal.jp2p.ui.property.databinding;

import net.osgi.jp2p.properties.IJp2pValidator;

public interface IJxseDatabinding<T, U> {

	public abstract void setValidator(IJp2pValidator<T, U> validator);

	/**
	 * Dispose the data binding
	 */
	public abstract void dispose();

}