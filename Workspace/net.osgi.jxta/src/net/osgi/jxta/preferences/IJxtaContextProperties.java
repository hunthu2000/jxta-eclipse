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
package net.osgi.jxta.preferences;

import java.util.Properties;

import net.osgi.jxta.utils.StringStyler;

public interface IJxtaContextProperties {

	public enum ContextProperties{
		NAME,
		PLUGIN_ID,
		HOME_FOLDER,
		CONFIG_MODE,
		PORT,
		PEER_ID,
		RENDEZVOUZ_AUTOSTART,
		TCP_ENABLED,
		TCP_INCOMING,
		TCP_OUTGOING,
		USE_MULTICAST;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
	}
	
	/**
	 * Get the property for the given context property key
	 * @param key
	 * @return
	 */
	public Object getProperty( ContextProperties key );
	
	/**
	 * Get the properties
	 * @return
	 */
	public Properties getProperties();
}
