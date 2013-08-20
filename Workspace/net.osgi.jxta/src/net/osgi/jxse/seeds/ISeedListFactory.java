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
package net.osgi.jxse.seeds;

import java.io.IOException;

import net.jxta.platform.NetworkConfigurator;

public interface ISeedListFactory {

	public abstract void createSeedlist(NetworkConfigurator configurator)
			throws IOException;
	
	public boolean isEmpty();

}