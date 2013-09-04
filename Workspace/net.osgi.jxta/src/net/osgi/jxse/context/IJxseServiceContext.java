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
package net.osgi.jxse.context;

import net.osgi.jxse.activator.IJxseService;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.utils.StringStyler;

public interface IJxseServiceContext<T extends Object> extends IJxseComponentNode<T>, IJxseService<T> {

	public enum ContextProperties{
		IDENTIFIER,
		PLUGIN_ID,
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

	public enum ContextDirectives{
		AUTO_START,
		PEER_ID_PERSIST,
		PEER_ID_CREATE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public String getIdentifier();
	
	public void setIdentifier( String identifier );
}