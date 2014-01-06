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
package net.osgi.jp2p.jxta.factory;

import net.osgi.jp2p.builder.ICompositeBuilderListener;
import net.osgi.jp2p.factory.IJp2pComponents;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.StringStyler;

public interface IJxtaComponentFactory<T extends Object> extends ICompositeBuilderListener<Object>{

	public enum JxtaComponents implements IJp2pComponents{
		NETWORK_MANAGER,
		NETWORK_CONFIGURATOR,
		SEED_LIST,
		SECURITY,
		TCP,
		HTTP,
		HTTP2,
		MULTICAST,
		NET_PEERGROUP_SERVICE,
		PEERGROUP_SERVICE,
		DISCOVERY_SERVICE,
		REGISTRATION_SERVICE,
		RENDEZVOUS_SERVICE,
		PIPE_SERVICE,
		JXSE_SOCKET,
		JXSE_SERVER_SOCKET,
		ADVERTISEMENT_SERVICE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
		
		public static boolean isComponent( String str ){
			str = StringStyler.styleToEnum(str);
			if(( str == null ) || ( str.length() == 0 ))
				return false;
			for( JxtaComponents comp: values()){
				if( comp.name().equals( str.toUpperCase() ))
					return true;
			}
			return false;
		}
	}

	/**
	 * Get the component name that will be created
	 * @return
	 */
	public String getComponentName();
	
	/**
	 * Get the weight of the factory. By default, the context factory is zero, startup service is one
	 * @return
	 */
	public int getWeight();
	
	/**
	 * Returns true if the factory can create its product
	 * @return
	 */
	public boolean canCreate();

	/**
	 * Get the property source
	 * @return
	 */
	public IJp2pPropertySource<IJp2pProperties> getPropertySource();

	/**
	 * Get the property source that is used for the factor
	 * @return
	 */
	public IJp2pPropertySource<IJp2pProperties> createPropertySource();
	
	/**
	 * This method is called after the property sources have been created,
	 * to allow other factories to be added as well.
	 */
	public void extendContainer();
		
	/**
	 * The completion is not necessarily the same as creating the module. This method has to 
	 * be called separately;
	 * @return
	 */
	public boolean complete();
	
	public boolean isCompleted();
	
	public boolean hasFailed();
	
	/**
	 * Get the service component. Returns null if the factory was not completed
	 * @return
	 */
	public T getComponent();
	
	/**
	 * Returns true if the module is activated
	 * @return
	 */
	public boolean componentActive();
}