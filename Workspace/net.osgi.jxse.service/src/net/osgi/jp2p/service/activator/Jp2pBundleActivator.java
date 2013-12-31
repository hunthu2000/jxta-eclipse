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
package net.osgi.jp2p.service.activator;

import net.osgi.jp2p.builder.ICompositeBuilderListener;
import net.osgi.jp2p.context.ContainerFactory;
import net.osgi.jp2p.context.Jp2pServiceContainer;
import net.osgi.jp2p.service.xml.XMLServiceBuilder;

public class Jp2pBundleActivator extends AbstractJxseBundleActivator {

	private String bundle_id;
	private ICompositeBuilderListener<?> observer;
	
	
	public Jp2pBundleActivator(String bundle_id) {
		this.bundle_id = bundle_id;
	}

	public ICompositeBuilderListener<?> getObserver() {
		return observer;
	}

	public void setObserver(ICompositeBuilderListener<?> observer) {
		this.observer = observer;
	}

	@Override
	protected Jp2pServiceContainer createContext() {
		XMLServiceBuilder builder = new XMLServiceBuilder( bundle_id, this.getClass() );
		if( observer != null )
			builder.addListener(observer);
		Jp2pServiceContainer container = builder.build();
		if( observer != null )
			builder.removeListener(observer);
		return container;
	}
}