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
package net.osgi.jxse.builder;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jxse.builder.ICompositeBuilderListener.BuilderEvents;
import net.osgi.jxse.component.ModuleNode;
import net.osgi.jxse.context.JxseServiceContext;
import net.osgi.jxse.factory.ComponentBuilderEvent;

public class CompositeBuilder implements ICompositeBuilder<ModuleNode<JxseServiceContext>> {

	private Collection<ICompositeBuilderListener<?>> builderlisteners;
	private ModuleNode<JxseServiceContext> root;
	
	public CompositeBuilder( ModuleNode<JxseServiceContext> root ) {
		this.root = root;
		this.builderlisteners = new ArrayList<ICompositeBuilderListener<?>>();
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#addListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void addListener( ICompositeBuilderListener<?> listener ){
		this.builderlisteners.add( listener);
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#removeListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	@Override
	public void removeListener( ICompositeBuilderListener<?> listener ){
		this.builderlisteners.remove( listener);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void notifyListeners( ComponentBuilderEvent event ){
		for( ICompositeBuilderListener<?> listener: builderlisteners )
			listener.notifyChange(event);
	}

	@Override
	public ModuleNode<JxseServiceContext> build() {
		this.extendPropertySources(root);
		this.parseModules(this.root, null );
		return this.root;
	}

	/**
	 * Extend the property sources, if this is required.
	 * @param node
	 */
	private final void extendPropertySources( ModuleNode<?> node){
		node.getData().extendModules();	
		for( ComponentNode<?> child: node.getChildren()) {
			this.extendPropertySources((ModuleNode<?>) child );
		}
	}

	/**
	 * Parse the modules that can be started immediately
	 * @param node
	 */
	@SuppressWarnings({"rawtypes", "unchecked" })
	private final void parseModules( ModuleNode<?> node, ModuleNode<?> parent ){
		if( !node.getData().canCreate() )
			return;
		node.getData().createFactory();	
		for( ComponentNode<?> child: node.getChildren()) {
			this.parseModules( (ModuleNode<?>) child, node );
		}
		this.notifyListeners( new ComponentBuilderEvent( this, node.getData(), BuilderEvents.FACTORY_CREATED ));
	}
}