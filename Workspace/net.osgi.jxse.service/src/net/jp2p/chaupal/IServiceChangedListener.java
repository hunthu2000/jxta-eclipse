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
package net.jp2p.chaupal;

import net.jp2p.container.utils.StringStyler;

public interface IServiceChangedListener {

	public enum ServiceChange{
		CHILD_ADDED,
		CHILD_REMOVED,
		STATUS_CHANGE,
		COMPONENT_EVENT,
		REFRESH;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	public void notifyServiceChanged( ServiceChangedEvent event );
}