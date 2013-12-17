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
package net.osgi.jxse.service.xml;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.ICompositeBuilder;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.context.Swarm;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.service.core.ChaupalCompositeBuilder;

public class XMLServiceBuilder implements ICompositeBuilder<ComponentNode<JxseServiceContext, IJxseProperties>, IJxseProperties>{

	private ComponentNode<JxseServiceContext,IJxseProperties> root;
	
	private String plugin_id;
	private Class<?> clss;
	private BuilderContainer container;
	private Swarm swarm;
	
	private Collection<ICompositeBuilderListener<?>> listeners;
	
	public XMLServiceBuilder( String plugin_id, Class<?> clss) {
		this.plugin_id = plugin_id;
		this.clss = clss;	
		this.listeners = new ArrayList<ICompositeBuilderListener<?>>();
		container = new BuilderContainer();

	}
	
	public JxseServiceContext getModule() {
		return root.getFactory().getModule();
	}

	@Override
	public ComponentNode<JxseServiceContext, IJxseProperties> build() {
		XMLPropertySourceBuilder builder = new XMLPropertySourceBuilder( plugin_id, clss, container );
		
		//First build the property sources
		this.addListenerToBuilder(builder);
		IJxsePropertySource<IJxseProperties> source = builder.build();
		this.removeListenerFromBuilder(builder);
		
		//Then build the factories
		ICompositeBuilder<ComponentNode<JxseServiceContext, IJxseProperties>, IJxseProperties> cf = 
				new ChaupalCompositeBuilder<ComponentNode<JxseServiceContext, IJxseProperties>, IJxseProperties>( source, container, swarm );
		this.addListenerToBuilder( cf );
		root = cf.build();
		this.removeListenerFromBuilder( cf );
		return this.root;
	}

	@Override
	public void addListener(ICompositeBuilderListener<?> listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(ICompositeBuilderListener<?> listener) {
		this.listeners.remove(listener);
	}

	private final void addListenerToBuilder(ICompositeBuilder<?,?> builder) {
		for( ICompositeBuilderListener<?> listener: this.listeners )
			builder.addListener(listener);
	}

	private void removeListenerFromBuilder(ICompositeBuilder<?,?> builder) {
		for( ICompositeBuilderListener<?> listener: this.listeners )
			builder.removeListener(listener);
	}

}