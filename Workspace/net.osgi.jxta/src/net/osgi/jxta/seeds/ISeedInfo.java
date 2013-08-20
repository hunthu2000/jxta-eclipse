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
package net.osgi.jxta.seeds;

import net.osgi.jxta.utils.StringStyler;

public interface ISeedInfo {

	public enum SeedTypes{
		RDV,
		RELAY;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}	
	}	
}
