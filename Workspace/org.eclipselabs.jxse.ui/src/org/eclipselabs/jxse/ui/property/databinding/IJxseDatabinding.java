package org.eclipselabs.jxse.ui.property.databinding;

import net.osgi.jxse.properties.IJxseValidator;

public interface IJxseDatabinding<T, U> {

	public abstract void setValidator(IJxseValidator<T, U> validator);

	/**
	 * Dispose the data binding
	 */
	public abstract void dispose();

}