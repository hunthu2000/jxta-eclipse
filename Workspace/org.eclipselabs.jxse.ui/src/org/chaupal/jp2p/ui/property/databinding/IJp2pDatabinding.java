package org.chaupal.jp2p.ui.property.databinding;

import net.jp2p.container.properties.IJp2pValidator;

public interface IJp2pDatabinding<T, U> {

	public abstract void setValidator(IJp2pValidator<T, U> validator);

	/**
	 * Dispose the data binding
	 */
	public abstract void dispose();

}