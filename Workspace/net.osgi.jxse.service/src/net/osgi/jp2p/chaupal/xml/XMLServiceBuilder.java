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

import net.jp2p.container.ContainerFactory;
import net.jp2p.container.Jp2pServiceContainer;
import net.jp2p.container.builder.ContainerBuilder;
import net.jp2p.container.builder.ICompositeBuilder;
import net.jp2p.container.builder.ICompositeBuilderListener;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.builder.IFactoryBuilder;
import net.jp2p.container.builder.ICompositeBuilderListener.BuilderEvents;
import net.jp2p.container.context.ContextLoader;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.factory.IComponentFactory;
import net.jp2p.container.factory.IPropertySourceFactory;

public class XMLServiceBuilder implements ICompositeBuilder<Jp2pServiceContainer>{

	private String plugin_id;
	private Class<?> clss;
	private Collection<ICompositeBuilder<ContainerFactory>> builders;
	private ContextLoader contexts;
	
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
	private void extendBuilders( Class<?> clss, IContainerBuilder containerBuilder ) throws IOException{
		Enumeration<URL> enm = clss.getClassLoader().getResources( IFactoryBuilder.S_DEFAULT_LOCATION );
		while( enm.hasMoreElements()){
			URL url = enm.nextElement();
			builders.add( new XMLFactoryBuilder( plugin_id, url, clss, containerBuilder, contexts ));
		}
	}
	
	@Override
	public Jp2pServiceContainer build() {
		
		//Object obj;
		//try {
		//	obj = this.getClass().getClassLoader().loadClass("net.osgi.jp2p.chaupal.jxta.factories.TestFactory");
		//	System.out.println("URL found: " + ( obj != null ));
		//} catch (ClassNotFoundException e1) {
		//	// TODO Auto-generated catch block
		//	e1.printStackTrace();
		//}
		
		//First register all the discovered builders
		ContainerBuilder containerBuilder = new ContainerBuilder();
		this.contexts = new ContextLoader();
		contexts.addContext( new Jp2pContext());

		try {
			this.extendBuilders( XMLFactoryBuilder.class, containerBuilder);
			this.extendBuilders(clss, containerBuilder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Build the container from the resource files
		for( ICompositeBuilder<ContainerFactory> builder: this.builders){
			this.addListenersToBuilder(builder);
			builder.build();
			this.removeListenersFromBuilder(builder);
		}

		//Extend the container with factories that are also needed
		this.extendContainer( containerBuilder );
		this.notifyPropertyCreated( containerBuilder);
		
		//Last create the container and the components
		ContainerFactory factory = (ContainerFactory) containerBuilder.getFactory( Jp2pContext.Components.JP2P_CONTAINER.toString() );
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
		IPropertySourceFactory<?>[] factories = containerBuilder.getFactories();
		for( IPropertySourceFactory<?> factory: factories ){
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
		for( IPropertySourceFactory<?> factory: containerBuilder.getFactories() ){
			containerBuilder.updateRequest( new ComponentBuilderEvent<Object>((IPropertySourceFactory<Object>) factory, BuilderEvents.PROPERTY_SOURCE_CREATED));
		}
	}

}
