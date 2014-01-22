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
package net.jp2p.jxta.seeds;

import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.factory.IComponentFactory;
import net.jxta.platform.NetworkConfigurator;

public interface ISeedListFactory extends IComponentFactory<IJp2pComponent<String>>{

	public abstract void setConfigurator( NetworkConfigurator configurator );
}