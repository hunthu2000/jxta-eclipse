package org.chaupal.jp2p.ui.property;

import org.eclipse.ui.views.properties.IPropertySource;

public interface IJp2pPropertySourceProvider<T extends Object> {

	/**
	 * Get the component name
	 * @return
	 */
	public String getComponentName();
	
	/**
	 * Get the descriptor
	 * @return
	 */
	public IPropertySource getPropertySource();
}
