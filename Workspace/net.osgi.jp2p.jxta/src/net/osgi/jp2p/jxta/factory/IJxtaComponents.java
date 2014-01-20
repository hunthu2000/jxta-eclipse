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

import net.osgi.jp2p.factory.IJp2pComponents;
import net.osgi.jp2p.utils.StringStyler;

public interface IJxtaComponents{

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
		DISCOVERY_SERVICE,
		PEERGROUP_SERVICE,
		REGISTRATION_SERVICE,
		RENDEZVOUS_SERVICE,
		JXSE_SOCKET,
		JXSE_SERVER_SOCKET,
		ADVERTISEMENT,
		PIPE_SERVICE;

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
}