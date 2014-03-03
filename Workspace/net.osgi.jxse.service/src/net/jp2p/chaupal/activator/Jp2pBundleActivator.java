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
package net.jp2p.chaupal.activator;

import net.jp2p.chaupal.xml.XMLServiceBuilder;
import net.jp2p.container.IJp2pContainer;
import net.jp2p.container.Jp2pContainer;
import net.jp2p.container.component.ComponentEventDispatcher;
import net.jp2p.container.component.IComponentChangedListener;
import net.jp2p.container.startup.Jp2pStartupService;

public class Jp2pBundleActivator extends AbstractJp2pBundleActivator<Jp2pStartupService> {

	private String bundle_id;
	private IComponentChangedListener observer;
	
	
	public Jp2pBundleActivator(String bundle_id) {
		this.bundle_id = bundle_id;
	}

	public IComponentChangedListener getObserver() {
		return observer;
	}

	public void setObserver(IComponentChangedListener observer) {
		this.observer = observer;
	}

	@Override
	protected IJp2pContainer<Jp2pStartupService> createContainer() {
		XMLServiceBuilder builder = new XMLServiceBuilder( bundle_id, this.getClass() );
		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		if( observer != null )
			dispatcher.addServiceChangeListener(observer);
		Jp2pContainer container = builder.build();
		if( observer != null )
			dispatcher.removeServiceChangeListener(observer);
		return container;
	}
}