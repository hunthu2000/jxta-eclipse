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

import net.osgi.jxse.preferences.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public interface IComponentFactory<T extends Object, U extends Enum<U>, V extends Enum<V>> {

	public enum Components{
		JXSE_CONTEXT,
		NETWORK_MANAGER,
		NETWORK_CONFIGURATOR,
		PEERGROUP_SERVICE,
		DISCOVERY_SERVICE,
		RENDEZVOUS_SERVICE,
		JXSE_SOCKET,
		JXSE_SERVER_SOCKET,
		ADVERTISEMENT;

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
	 * Directives give additional clues on how to create the component
	 * @author Kees
	 *
	 */
	public enum Directives{
		AUTO_START,
		CLEAR_CONFIG,
		CREATE_PARENT,
		ACTIVATE_PARENT,
		PASS1,
		PASS2;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isDirective( String str ){
			str = StringStyler.styleToEnum(str);
			if(( str == null ) || ( str.length() == 0 ))
				return false;
			for( Directives comp: values()){
				if( comp.name().equals( str.toUpperCase() ))
					return true;
			}
			return false;
		}
	}

	/**
	 * Get the name of the component that is created
	 * @return
	 */
	public Components getComponentName();
	
	/**
	 * Returns true if the factory can create its product
	 * @return
	 */
	public boolean canCreate();

	/**
	 * Get the property source
	 * @return
	 */
	public IJxsePropertySource<U,V> getPropertySource();

	/**
	 * First time creation of the service component.
	 * @return
	 */
	public T createModule();
	
	public boolean isCompleted();
	
	public boolean hasFailed();
	
	/**
	 * Get the service component. Returns null if the factory was not completed
	 * @return
	 */
	public T getModule();
}
