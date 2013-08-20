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
package net.osgi.jxta.context;

import net.osgi.jxta.activator.IJxtaService;
import net.osgi.jxta.component.IJxtaComponentNode;
import net.osgi.jxta.utils.StringStyler;

public interface IJxtaServiceContext<T extends Object> extends IJxtaComponentNode<T>, IJxtaService<T> {

	public enum ContextProperties{
		IDENTIFIER;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	public String getIdentifier();
	
	public void setIdentifier( String identifier );
}