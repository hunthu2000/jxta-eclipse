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
package net.osgi.jxse.service.activator;

import net.osgi.jxse.activator.JxseStartupService;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.service.xml.XMLServiceBuilder;

public class JxseBundleActivator extends AbstractJxseBundleActivator {

	private String bundle_id;
	private ICompositeBuilderListener<?> observer;
	
	
	public JxseBundleActivator(String bundle_id) {
		this.bundle_id = bundle_id;
	}

	public ICompositeBuilderListener<?> getObserver() {
		return observer;
	}

	public void setObserver(ICompositeBuilderListener<?> observer) {
		this.observer = observer;
	}

	@Override
	protected JxseServiceContext createContext() {
		XMLServiceBuilder builder = new XMLServiceBuilder( bundle_id, this.getClass() );
		if( observer != null )
			builder.addListener(observer);
		ComponentNode<JxseServiceContext, IJxseProperties, IJxseDirectives> node = builder.build();
		if( observer != null )
			builder.removeListener(observer);
		JxseStartupService service = getStartupService( node);
		service.activate();
		return node.getFactory().getModule();
	}
	
	/**
	 * Get the startup service 
	 * @param context
	 * @return
	 */
	protected static JxseStartupService getStartupService( ComponentNode<JxseServiceContext, IJxseProperties, IJxseDirectives> context ){
		for( ComponentNode<?,?,?> node: context.getChildren() ){
			if( node.getFactory().getComponent().equals( Components.STARTUP_SERVICE )){
				return (JxseStartupService) node.getFactory().createModule();
			}
		}
		return null;
	}
}
