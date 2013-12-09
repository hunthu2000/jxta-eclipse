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
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.service.core.JxseCompositeBuilder;

public class XMLServiceBuilder implements ICompositeBuilder<ComponentNode<JxseServiceContext, IJxseProperties, IJxseDirectives>, IJxseProperties, IJxseDirectives>{

	private ComponentNode<JxseServiceContext,IJxseProperties, IJxseDirectives> root;
	
	private String plugin_id;
	private Class<?> clss;
	private Swarm swarm;
	private BuilderContainer builder;
	
	private Collection<ICompositeBuilderListener<?>> listeners;
	
	public XMLServiceBuilder( String plugin_id, Class<?> clss) {
		this.plugin_id = plugin_id;
		this.clss = clss;	
		this.swarm = new Swarm();
		this.listeners = new ArrayList<ICompositeBuilderListener<?>>();
		builder = new BuilderContainer();
	}
	
	public JxseServiceContext getModule() {
		return root.getFactory().getModule();
	}

	@Override
	public ComponentNode<JxseServiceContext, IJxseProperties, IJxseDirectives> build() {
		XMLPropertySourceBuilder builder = new XMLPropertySourceBuilder( plugin_id, clss );
		
		//First build the property sources
		this.addListenerToBuilder(builder);
		IJxsePropertySource<IJxseProperties, IJxseDirectives> source = builder.build();
		this.removeListenerFromBuilder(builder);
		
		//Then build the factories
		ICompositeBuilder<ComponentNode<JxseServiceContext, IJxseProperties, IJxseDirectives>, IJxseProperties, IJxseDirectives> cf = 
				new JxseCompositeBuilder<ComponentNode<JxseServiceContext, IJxseProperties, IJxseDirectives>, IJxseProperties, IJxseDirectives>( source );
		this.addListenerToBuilder( cf );
		root = cf.build();
		this.removeListenerFromBuilder( cf );
		/*
		super.setIdentifier( builder.getPropertySource().getIdentifier() );
		super.setProperties( (IJxseWritePropertySource<IJxseProperties, IJxseDirectives>) root.getFactory().getPropertySource() );
		super.putProperty( ContextProperties.PASS_1, 
				builder.getPropertySource().getProperty( ContextProperties.PASS_1 ));
		super.putProperty( ContextProperties.PASS_2, 
				builder.getPropertySource().getProperty( ContextProperties.PASS_2 ));
		*/
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

	private final void addListenerToBuilder(ICompositeBuilder<?,?,?> builder) {
		for( ICompositeBuilderListener<?> listener: this.listeners )
			builder.addListener(listener);
	}

	private void removeListenerFromBuilder(ICompositeBuilder<?,?,?> builder) {
		for( ICompositeBuilderListener<?> listener: this.listeners )
			builder.removeListener(listener);
	}

}