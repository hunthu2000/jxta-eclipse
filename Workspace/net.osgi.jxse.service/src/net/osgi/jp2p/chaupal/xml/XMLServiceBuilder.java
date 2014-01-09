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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.builder.ICompositeBuilder;
import net.osgi.jp2p.builder.ICompositeBuilderListener;
import net.osgi.jp2p.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jp2p.container.ContainerFactory;
import net.osgi.jp2p.container.Jp2pServiceContainer;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.factory.IComponentFactory.Components;

public class XMLServiceBuilder implements ICompositeBuilder<Jp2pServiceContainer>{

	private String plugin_id;
	private Class<?> clss;
	private Collection<ICompositeBuilder<ContainerFactory>> builders;
	
	private Collection<ICompositeBuilderListener<?>> listeners;
	
	public XMLServiceBuilder( String plugin_id, Class<?> clss) {
		this.plugin_id = plugin_id;
		this.clss = clss;	
		builders = new ArrayList<ICompositeBuilder<ContainerFactory>>();
		this.listeners = new ArrayList<ICompositeBuilderListener<?>>();
	}
	
	/**
	 * Allow additional builders to extend the primary builder, by looking at resources with the
	 * similar name and location, for instance provided by fragments
	 * @param clss
	 * @param containerBuilder
	 * @throws IOException
	 */
	private void extendBuilders( Class<?> clss, ContainerBuilder containerBuilder ) throws IOException{
		Enumeration<URL> enm = clss.getClassLoader().getResources( XMLFactoryBuilder.S_DEFAULT_LOCATION );
		while( enm.hasMoreElements()){
			URL url = enm.nextElement();
			builders.add( new XMLFactoryBuilder( plugin_id, url, containerBuilder ));
		}
	}
	
	@Override
	public Jp2pServiceContainer build() {
		//First build the property sources
		ContainerBuilder containerBuilder = new ContainerBuilder();
		try {
			this.extendBuilders(clss, containerBuilder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for( ICompositeBuilder<ContainerFactory> builder: this.builders){
			this.addListenersToBuilder(builder);
			builder.build();
			this.removeListenersFromBuilder(builder);
		}

		//Extend the container with factories that are also needed
		this.extendContainer( containerBuilder );
		this.notifyPropertyCreated( containerBuilder);
		
		//Last create the container and the components
		ContainerFactory factory = (ContainerFactory) containerBuilder.getFactory( Components.JP2P_CONTAINER.toString() );
		return factory.createComponent();
	}


	@Override
	public void addListener(ICompositeBuilderListener<?> listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(ICompositeBuilderListener<?> listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Delay the addition of builders until the parsing starts
	 * @param builder
	 */
	private final void addListenersToBuilder(ICompositeBuilder<?> builder) {
		for( ICompositeBuilderListener<?> listener: this.listeners )
			builder.addListener(listener);
	}

	/**
	 * Remove the listeners from the builder
	 * @param builder
	 */
	private void removeListenersFromBuilder(ICompositeBuilder<?> builder) {
		for( ICompositeBuilderListener<?> listener: this.listeners )
			builder.removeListener(listener);
	}

	/**
	 * Notify that the property sources have been created after parsing the XML
	 * file. This allows for more fine-grained tuning of the property sources
	 * @param node
	 */
	private void extendContainer( ContainerBuilder containerBuilder){
		IComponentFactory<?>[] factories = containerBuilder.getChildren();
		for( IComponentFactory<?> factory: factories ){
			factory.extendContainer();
		}
	}

	/**
	 * Notify that the property sources have been created after parsing the XML
	 * file. This allows for more fine-grained tuning of the property sources
	 * @param node
	 */
	@SuppressWarnings({ "unchecked" })
	private void notifyPropertyCreated( ContainerBuilder containerBuilder){
		for( IComponentFactory<?> factory: containerBuilder.getChildren() ){
			containerBuilder.updateRequest( new ComponentBuilderEvent<Object>((IComponentFactory<Object>) factory, BuilderEvents.PROPERTY_SOURCE_CREATED));
		}
	}

}