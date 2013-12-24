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

import net.osgi.jxse.builder.ICompositeBuilder;
import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.builder.container.BuilderContainer;
import net.osgi.jxse.component.ModuleNode;
import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.service.core.ChaupalCompositeBuilder;

public class XMLServiceBuilder implements ICompositeBuilder<ModuleNode<JxseServiceContext>>{

	private ModuleNode<JxseServiceContext> root;
	
	private String plugin_id;
	private Class<?> clss;
	private BuilderContainer container;
	
	private Collection<ICompositeBuilderListener<?>> listeners;
	
	public XMLServiceBuilder( String plugin_id, Class<?> clss) {
		this.plugin_id = plugin_id;
		this.clss = clss;	
		this.listeners = new ArrayList<ICompositeBuilderListener<?>>();
		container = new BuilderContainer();
	}
	
	@Override
	public ModuleNode<JxseServiceContext> build() {
		//First build the property sources
		XMLPropertySourceBuilder builder = new XMLPropertySourceBuilder( plugin_id, clss, container );		
		this.addListenerToBuilder(builder);
		builder.addListener(container);
		ModuleNode<JxseServiceContext> node = builder.build();
		builder.removeListener(container);
		this.removeListenerFromBuilder(builder);
		
		//Then build the factories
		ICompositeBuilder<ModuleNode<JxseServiceContext>> cf = new ChaupalCompositeBuilder( node );
		this.addListenerToBuilder( cf );
		cf.addListener(container);
		this.root = cf.build();
		cf.removeListener(container);
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

	private final void addListenerToBuilder(ICompositeBuilder<?> builder) {
		for( ICompositeBuilderListener<?> listener: this.listeners )
			builder.addListener(listener);
	}

	private void removeListenerFromBuilder(ICompositeBuilder<?> builder) {
		for( ICompositeBuilderListener<?> listener: this.listeners )
			builder.removeListener(listener);
	}

}