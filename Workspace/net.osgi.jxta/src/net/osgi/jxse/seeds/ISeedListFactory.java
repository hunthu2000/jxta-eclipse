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

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.factory.IComponentFactory;

public interface ISeedListFactory extends IComponentFactory<IJxseComponent<String>>{

	public abstract void setConfigurator( NetworkConfigurator configurator );
}