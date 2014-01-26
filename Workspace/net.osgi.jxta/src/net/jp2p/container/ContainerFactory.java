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

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.AbstractComponentFactory;
import net.jp2p.container.factory.ComponentBuilderEvent;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.container.startup.Jp2pStartupService;

public class ContainerFactory extends AbstractComponentFactory<Jp2pStartupService>
{
	private String bundleId;
	
	public ContainerFactory( IContainerBuilder container, String bundleId) {
		super(container );
		super.setCanCreate(true);
		this.bundleId = bundleId;
	}	

	@Override
	public String getComponentName() {
		return Jp2pContext.Components.JP2P_CONTAINER.toString();
	}
	
	@Override
	protected Jp2pContainerPropertySource onCreatePropertySource() {
		return new Jp2pContainerPropertySource( this.bundleId );
	}

	@Override
	public void extendContainer() {
		this.onPropertySourceCreated();
		super.extendContainer();	
	}

	
	@Override
	public Jp2pContainer createComponent() {
		return (Jp2pContainer) super.createComponent();
	}

	@Override
	protected Jp2pContainer onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		Jp2pContainer context = new Jp2pContainer( super.getPropertySource() );
		return context;
	}

	//Make public
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		super.notifyChange(event);
	}

	/**
	 * Returns true if the context can be autostarted 
	 * @return
	 */
	public boolean isAutoStart(){
		IContainerBuilder container = super.getBuilder();
		boolean autostart = Jp2pContainerPropertySource.isAutoStart(this.getPropertySource());
		if( autostart)
			return true;
		String comp = Jp2pContext.Components.STARTUP_SERVICE.toString();
		IPropertySourceFactory<?> startup = container.getFactory( comp );
		return (( startup != null ) &&  Jp2pContainerPropertySource.isAutoStart(startup.getPropertySource() ));
	}

	@SuppressWarnings("unchecked")
	private void onPropertySourceCreated(){
		IContainerBuilder builder = super.getBuilder();
		boolean autostart = Jp2pContainerPropertySource.isAutoStart(this.getPropertySource());
		String comp = Jp2pContext.Components.STARTUP_SERVICE.toString();
		IPropertySourceFactory<?> startup = builder.getFactory( comp );
		if( !autostart || ( startup != null ))
			return;
		
		startup = Jp2pContext.getDefaultFactory( builder, this.getPropertySource(), comp);
		IJp2pWritePropertySource<IJp2pProperties> props = (IJp2pWritePropertySource<IJp2pProperties>) startup.createPropertySource();
		props.setDirective( Directives.AUTO_START, Boolean.TRUE.toString());
		builder.addFactory((IPropertySourceFactory<Object>) startup);
	}
}
