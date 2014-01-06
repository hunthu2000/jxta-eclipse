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
package net.osgi.jp2p.chaupal.xml;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.builder.ICompositeBuilder;
import net.osgi.jp2p.builder.ICompositeBuilderListener;
import net.osgi.jp2p.container.Jp2pServiceContainer;

public class XMLServiceBuilder implements ICompositeBuilder<Jp2pServiceContainer>{

	private Jp2pServiceContainer container;
	
	private String plugin_id;
	private Class<?> clss;
	private ContainerBuilder builder;
	
	private Collection<ICompositeBuilderListener<?>> listeners;
	
	public XMLServiceBuilder( String plugin_id, Class<?> clss) {
		this.plugin_id = plugin_id;
		this.clss = clss;	
		this.listeners = new ArrayList<ICompositeBuilderListener<?>>();
		builder = new ContainerBuilder();
	}
	
	@Override
	public Jp2pServiceContainer build() {
		//First build the property sources
		XMLFactoryBuilder xmlbuilder = new XMLFactoryBuilder( plugin_id, clss, builder );		
		this.addListenerToBuilder(xmlbuilder);
		this.container = xmlbuilder.build();

		this.removeListenerFromBuilder(xmlbuilder);
		return this.container;
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