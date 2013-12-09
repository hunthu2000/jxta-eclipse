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
import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class StartupServiceFactory extends AbstractComponentFactory<JxseStartupService, IJxseProperties, IJxseDirectives>
{
	
	private ComponentNode<JxseServiceContext, IJxseProperties, IJxseDirectives> root;
	
	public StartupServiceFactory( ComponentNode<JxseServiceContext, IJxseProperties, IJxseDirectives> root, JxseStartupPropertySource source) {
		super(source );
		this.root = root;
	}
	
	@Override
	protected JxseStartupService onCreateModule( IJxsePropertySource<IJxseProperties, IJxseDirectives> properties) {
		return new JxseStartupService( root, (JxseStartupPropertySource) super.getPropertySource() );
	}
}
