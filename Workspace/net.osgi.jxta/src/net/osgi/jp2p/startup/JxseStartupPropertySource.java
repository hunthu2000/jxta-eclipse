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
package net.osgi.jp2p.startup;

import net.osgi.jp2p.context.Jp2pContainerPropertySource;
import net.osgi.jp2p.context.IJxseServiceContainer.ContextProperties;
import net.osgi.jp2p.factory.IComponentFactory.Components;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.utils.StringStyler;

public class JxseStartupPropertySource extends AbstractJp2pWritePropertySource{
	
	public enum StartupProperties implements IJp2pProperties{
		RETRIES;

		@Override
		public String toString() {
			return StringStyler.prettyString(super.toString());
		}
	}
	
	public JxseStartupPropertySource( Jp2pContainerPropertySource parent ) {
		super( Components.STARTUP_SERVICE.toString(), parent );
		super.setDirective( Directives.AUTO_START, parent.getDirective( Directives.AUTO_START ));
		super.setProperty( StartupProperties.RETRIES, 10 );
	}

	@Override
	public IJp2pProperties getIdFromString(String key) {
		return ContextProperties.valueOf( key );
	}

	@Override
	public Object getDefault( IJp2pProperties id) {
		if(!( id instanceof ContextProperties ))
			return null;
		ContextProperties cp = (ContextProperties )id;
		switch( cp ){
		default:
			break;
		}
		return null;
	}

	@Override
	public boolean validate( IJp2pProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
