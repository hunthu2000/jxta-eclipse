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
package net.osgi.jp2p.context;

import net.jxta.platform.NetworkManager;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.factory.AbstractComponentFactory;
import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class ContainerFactory extends AbstractComponentFactory<NetworkManager>
{
	private String bundleId;
	
	public ContainerFactory( ContainerBuilder container, String bundleId) {
		super(container, true );
		this.bundleId = bundleId;
	}	

	@Override
	public String getComponentName() {
		return Components.JXSE_CONTEXT.toString();
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
	public Jp2pServiceContainer createComponent() {
		return (Jp2pServiceContainer) super.createComponent();
	}

	@Override
	protected Jp2pServiceContainer onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		Jp2pServiceContainer context = new Jp2pServiceContainer( super.getPropertySource() );
		return context;
	}

	
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		super.notifyChange(event);
	}

	/**
	 * Returns true if the context can be autostarted 
	 * @return
	 */
	public boolean isAutoStart(){
		ContainerBuilder container = super.getBuilder();
		boolean autostart = Jp2pContainerPropertySource.isAutoStart(this.getPropertySource());
		if( autostart)
			return true;
		IComponentFactory<?> nm = container.getFactory( Components.NETWORK_MANAGER.toString() );
		if(nm != null ){
			autostart = Jp2pContainerPropertySource.isAutoStart(nm.getPropertySource());
			if( autostart )
				return true;
		}
		String comp = Components.STARTUP_SERVICE.toString();
		IComponentFactory<?> startup = container.getFactory( comp );
		return (( startup != null ) &&  Jp2pContainerPropertySource.isAutoStart(startup.getPropertySource() ));
	}

	@SuppressWarnings("unchecked")
	private void onPropertySourceCreated(){
		ContainerBuilder container = super.getBuilder();
		boolean autostart = Jp2pContainerPropertySource.isAutoStart(this.getPropertySource());
		IComponentFactory<?> nm = container.getFactory( Components.NETWORK_MANAGER.toString() );
		if(!autostart && ( nm == null ))
			return;
		autostart |= Jp2pContainerPropertySource.isAutoStart(nm.getPropertySource());
		String comp = Components.STARTUP_SERVICE.toString();
		IComponentFactory<?> startup = container.getFactory( comp );
		if( !autostart || ( startup != null ))
			return;
		
		startup = container.getDefaultFactory( this.getPropertySource(), comp);
		IJp2pWritePropertySource<IJp2pProperties> props = (IJp2pWritePropertySource<IJp2pProperties>) startup.createPropertySource();
		props.setDirective( Directives.AUTO_START, Boolean.TRUE.toString());
		container.addFactory((IComponentFactory<Object>) startup);
	}
}
