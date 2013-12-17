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
package net.osgi.jxse.peergroup;

import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.utils.StringStyler;

public interface IPeerGroupProperties {

	public enum PeerGroupProperties implements IJxseProperties{
		NAME,
		IDENTIFIER,
		PUBLISH,
		PEER_GROUP_ID,
		RENDEZ_VOUS;

	@Override
	public String toString() {
		return StringStyler.prettyString( super.toString() );
	}}

}
