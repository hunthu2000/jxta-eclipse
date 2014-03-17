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

import java.util.ArrayList;
import java.util.Collection;

import org.osgi.framework.BundleContext;

import net.jp2p.chaupal.context.Jp2pContextService;
import net.jp2p.container.component.ComponentEventDispatcher;
import net.jp2p.container.component.IComponentChangedListener;
import net.jp2p.container.context.ContextLoader;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.startup.Jp2pStartupService;

public class Jp2pBundleActivator extends AbstractJp2pBundleActivator<Jp2pStartupService> {

	private String bundle_id;
	private IComponentChangedListener observer;
	
	private BundleContext bundleContext;
	
	private static Jp2pContextService contextService; 
	private ContextLoader contextLoader;
	
	private Collection<IContainerBuilderListener> listeners;

	public Jp2pBundleActivator(String bundle_id) {
		this.bundle_id = bundle_id;
		listeners = new ArrayList<IContainerBuilderListener>();
	}

	public String getBundleId() {
		return bundle_id;
	}

	public IComponentChangedListener getObserver() {
		return observer;
	}

	public void setObserver(IComponentChangedListener observer) {
		this.observer = observer;
	}

	public void addContainerBuilderListener( IContainerBuilderListener listener ){
		listeners.add( listener );
	}

	public void removeContainerBuilderListener( IContainerBuilderListener listener ){
		listeners.remove( listener );
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		this.bundleContext = bundleContext;
		super.start(bundleContext);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		contextService.close();
		contextService = null;
		contextLoader.clear();
		contextLoader = null;

		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		if( observer != null )
			dispatcher.removeServiceChangeListener(observer);

		super.stop(bundleContext);
	}

	@Override
	protected void createContainer() {
		
		contextLoader = ContextLoader.getInstance();
		contextService = new Jp2pContextService( contextLoader, bundleContext );
		Jp2pContainerBuilderHelper helper = new Jp2pContainerBuilderHelper( this, contextLoader );
		for( IContainerBuilderListener listener: listeners )
			helper.addContainerBuilderListener(listener);
		helper.open();

		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		if( observer != null )
			dispatcher.addServiceChangeListener(observer);
		
		//Add contexts, both default as the ones provided through DS
		contextLoader.addContext( new Jp2pContext());
		contextService.open();				
	}
}