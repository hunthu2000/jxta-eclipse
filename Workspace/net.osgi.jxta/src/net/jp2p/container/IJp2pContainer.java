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
package net.jp2p.container;

import net.jp2p.container.activator.IJp2pService;
import net.jp2p.container.component.IJp2pComponentNode;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.utils.StringStyler;

public interface IJp2pContainer<T extends Object> extends IJp2pComponentNode<T>, IJp2pService<T> {

	/**
	 * The properties supported by the container
	 * @author Kees
	 *
	 */
	public enum ContainerProperties implements IJp2pProperties{
		BUNDLE_ID,
		HOME_FOLDER,
		PASS_1,
		PASS_2;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	/**
	 * The identifier of the container
	 * @return
	 */
	public String getIdentifier();
}