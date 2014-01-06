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
package net.osgi.jp2p.container;

import net.osgi.jp2p.activator.IJp2pService;
import net.osgi.jp2p.component.IJp2pComponentNode;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.utils.StringStyler;

public interface IJxseServiceContainer<T extends Object> extends IJp2pComponentNode<T>, IJp2pService<T> {

	public enum ContextProperties implements IJp2pProperties{
		BUNDLE_ID,
		HOME_FOLDER,
		CONFIG_MODE,
		PORT,
		PEER_ID,
		RENDEZVOUZ_AUTOSTART,
		PASS_1,
		PASS_2;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public String getIdentifier();
	
	/**
	 * Get the swarm of this context
	 * @return
	 */
	public Swarm getSwarm();
}