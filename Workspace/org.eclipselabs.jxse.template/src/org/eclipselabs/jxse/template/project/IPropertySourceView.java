/*******************************************************************************
 * Copyright (c) 2013 Condast and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kees Pieters - initial API and implementation
 *******************************************************************************/
package org.eclipselabs.jxse.template.project;

import net.osgi.jxse.context.JxseContextPropertySource;

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