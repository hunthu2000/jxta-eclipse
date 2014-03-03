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
package net.jp2p.chaupal.core;

import net.jp2p.chaupal.xml.XMLServiceBuilder;
import net.jp2p.container.IJp2pContainer;
import net.jp2p.container.IJp2pContainer.ContainerProperties;
import net.jp2p.container.component.ComponentEventDispatcher;
import net.jp2p.container.utils.Utils;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;

public class Jp2pDS2Component extends AbstractAttendeeProviderComponent {

	public static final String S_IJP2P_CONTAINER_PACKAGE_ID = "org.osgi.jxse.service.core";
	public static final String S_IP2P_TOKEN = "org.osgi.jxse.token";
	
	private Jp2pContainerProvider provider;
	private String introduction;
	private String token;

	protected Jp2pDS2Component( String bundle_id ) {
		this( bundle_id, S_IJP2P_CONTAINER_PACKAGE_ID, S_IP2P_TOKEN);
	}

	protected Jp2pDS2Component( String bundle_id, String introduction, String token) {
		this.token = token;
		this.introduction = introduction;
		//this.setActivator( bundle_id );
	}

	protected IJp2pContainer<?> getContainer() {
		return provider.getContainer();
	}

	public final void setActivator( String bundle_id ) {
		try{
			XMLServiceBuilder builder = new XMLServiceBuilder( bundle_id, this.getClass() );
			ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
			//if( observer != null )
			//	dispatcher.addServiceChangeListener(observer);
			IJp2pContainer<?> container = builder.build();
			//if( observer != null )
			//	dispatcher.removeServiceChangeListener(observer);
			String pass = (String) container.getPropertySource().getProperty( ContainerProperties.PASS_1);
			if( !Utils.isNull( pass ))
				this.introduction = pass;
			pass = (String) container.getPropertySource().getProperty( ContainerProperties.PASS_2);
			if( !Utils.isNull( pass ))
				this.token = pass;
			provider = new Jp2pContainerProvider( introduction, token );
			provider.setContainer(container);
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	@Override
	protected void initialise() {
		super.addAttendee( this.provider );
	}
}