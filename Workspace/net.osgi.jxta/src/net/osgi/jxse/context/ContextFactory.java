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
package net.osgi.jxse.context;

import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class ContextFactory extends AbstractComponentFactory<JxseServiceContext>
{
	public ContextFactory(JxseContextPropertySource source) {
		super(source );
	}	

	@Override
	protected JxseServiceContext onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		return new JxseServiceContext( super.getPropertySource() );
	}
}
