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
package net.osgi.jxse.activator;

import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class StartupServiceFactory extends AbstractComponentFactory<JxseStartupService, IJxseProperties> implements ICompositeBuilderListener<ComponentNode<?,IJxseProperties>>
{
	
	private ComponentNode<JxseServiceContext, IJxseProperties> root;
	
	public StartupServiceFactory( JxseStartupPropertySource source) {
		super(source );
	}
	
	@Override
	protected JxseStartupService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		return new JxseStartupService( root, (JxseStartupPropertySource) super.getPropertySource() );
	}

	@Override
	public void notifyCreated(
			ComponentBuilderEvent<ComponentNode<?, IJxseProperties>> event) {
		if(!( event.getComponent() instanceof IJxseServiceContext ))
			return;
	}
}
