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
package net.osgi.jxse.factory;

import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public interface IComponentFactory<T extends Object, U extends Object> {

	public enum Components{
		JXSE_CONTEXT,
		STARTUP_SERVICE,
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
			for( Components comp: values()){
				if( comp.name().equals( str.toUpperCase() ))
					return true;
			}
			return false;
		}
	}

	/**
	 * Get the component that is created
	 * @return
	 */
	public Components getComponent();
	
	/**
	 * Returns true if the factory can create its product
	 * @return
	 */
	public boolean canCreate();

	/**
	 * Get the property source
	 * @return
	 */
	public IJxsePropertySource<U> getPropertySource();

	/**
	 * First time creation of the service component.
	 * @return
	 */
	public T createModule();
	
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
	public T getModule();
	
	/**
	 * Returns true if the module is activated
	 * @return
	 */
	public boolean moduleActive();
}
