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
package net.jp2p.container.startup;

import net.jp2p.container.Jp2pContainerPropertySource;
import net.jp2p.container.IJxseServiceContainer.ContextProperties;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.properties.AbstractJp2pWritePropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.container.utils.StringStyler;

public class Jp2pStartupPropertySource extends AbstractJp2pWritePropertySource{
	
	public enum StartupProperties implements IJp2pProperties{
		RETRIES;

		@Override
		public String toString() {
			return StringStyler.prettyString(super.toString());
		}
	}
	
	public Jp2pStartupPropertySource( Jp2pContainerPropertySource parent ) {
		super( Jp2pContext.Components.STARTUP_SERVICE.toString(), parent );
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
