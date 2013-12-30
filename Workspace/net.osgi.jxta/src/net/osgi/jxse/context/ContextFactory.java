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

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class ContextFactory extends AbstractComponentFactory<NetworkManager>
{
	private String bundleId;
	
	public ContextFactory( BuilderContainer container, String bundleId) {
		super(container, true );
		this.bundleId = bundleId;
	}	

	@Override
	public String getComponentName() {
		return Components.JXSE_CONTEXT.toString();
	}
	
	@Override
	protected JxseContextPropertySource onCreatePropertySource() {
		return new JxseContextPropertySource( this.bundleId );
	}

	@Override
	public void extendContainer() {
		this.onPropertySourceCreated();
		super.extendContainer();	
	}

	
	@Override
	public JxseServiceContext createComponent() {
		return (JxseServiceContext) super.createComponent();
	}

	@Override
	protected JxseServiceContext onCreateComponent( IJxsePropertySource<IJxseProperties> properties) {
		JxseServiceContext context = new JxseServiceContext( super.getPropertySource() );
		return context;
	}

	
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		super.notifyChange(event);
	}

	/**
	 * Returns true if the context can be autostarted 
	 * @return
	 */
	public boolean isAutoStart(){
		BuilderContainer container = super.getContainer();
		boolean autostart = JxseContextPropertySource.isAutoStart(this.getPropertySource());
		if( autostart)
			return true;
		IComponentFactory<?> nm = container.getFactory( Components.NETWORK_MANAGER.toString() );
		if(nm != null ){
			autostart = JxseContextPropertySource.isAutoStart(nm.getPropertySource());
			if( autostart )
				return true;
		}
		String comp = Components.STARTUP_SERVICE.toString();
		IComponentFactory<?> startup = container.getFactory( comp );
		return (( startup != null ) &&  JxseContextPropertySource.isAutoStart(startup.getPropertySource() ));
	}

	@SuppressWarnings("unchecked")
	private void onPropertySourceCreated(){
		BuilderContainer container = super.getContainer();
		boolean autostart = JxseContextPropertySource.isAutoStart(this.getPropertySource());
		IComponentFactory<?> nm = container.getFactory( Components.NETWORK_MANAGER.toString() );
		if(!autostart && ( nm == null ))
			return;
		autostart |= JxseContextPropertySource.isAutoStart(nm.getPropertySource());
		String comp = Components.STARTUP_SERVICE.toString();
		IComponentFactory<?> startup = container.getFactory( comp );
		if( !autostart || ( startup != null ))
			return;
		
		startup = container.getDefaultFactory( this.getPropertySource(), comp);
		IJxseWritePropertySource<IJxseProperties> props = (IJxseWritePropertySource<IJxseProperties>) startup.createPropertySource();
		props.setDirective( Directives.AUTO_START, Boolean.TRUE.toString());
		container.addFactory((IComponentFactory<Object>) startup);
	}
}
