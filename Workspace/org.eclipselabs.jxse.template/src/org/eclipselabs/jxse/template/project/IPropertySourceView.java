package org.eclipselabs.jxse.template.project;

import net.osgi.jxse.preferences.properties.JxseContextPropertySource;

public interface IPropertySourceView {

	/**
//	 * Get the property source for this view
	 * @return
	 */
	public abstract JxseContextPropertySource getPropertySource();

	/**
	 * Complete the view by filling in the properties and directives
	 */
	public boolean complete() throws Exception;
}