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

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.peer.PeerID;
import net.jxta.platform.NetworkManager.ConfigMode;

public interface IJxseContextPreferences {

	public abstract boolean getRendezVousAutostart();

	public abstract ConfigMode getConfigMode();

	public abstract URI getHomeFolder() throws URISyntaxException;

	public abstract PeerID getPeerID() throws URISyntaxException;

}