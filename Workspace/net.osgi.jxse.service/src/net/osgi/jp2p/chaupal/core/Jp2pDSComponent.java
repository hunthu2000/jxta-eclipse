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
package net.osgi.jp2p.chaupal.core;

import net.jp2p.container.Jp2pServiceContainer;
import net.jp2p.container.IJxseServiceContainer.ContextProperties;
import net.jp2p.container.utils.Utils;
import net.osgi.jp2p.chaupal.activator.AbstractJp2pBundleActivator;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;
import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractProvider;


public class Jp2pDSComponent extends AbstractAttendeeProviderComponent {

	public static final String S_IJP2P_CONTAINER_PACKAGE_ID = "org.osgi.jxse.service.core";
	public static final String S_IP2P_TOKEN = "org.osgi.jxse.token";
	
	private Jp2pContainerProvider provider;
	private String introduction;
	private String token;

	protected Jp2pDSComponent() {}

	protected Jp2pDSComponent( AbstractJp2pBundleActivator activator ) {
		this( S_IJP2P_CONTAINER_PACKAGE_ID, S_IP2P_TOKEN, activator);
	}

	protected Jp2pDSComponent( String introduction, String token, AbstractJp2pBundleActivator activator ) {
		this.token = token;
		this.introduction = introduction;
		this.setActivator(activator);
	}

	protected Jp2pServiceContainer getContainer() {
		return provider.getContainer();
	}

	private final void setActivator(AbstractJp2pBundleActivator activator) {
		try{
			Jp2pServiceContainer container = activator.getServiceContainer();
			String pass = (String) container.getProperty( ContextProperties.PASS_1);
			if( !Utils.isNull( pass ))
				this.introduction = pass;
			pass = (String) container.getProperty( ContextProperties.PASS_2);
			if( !Utils.isNull( pass ))
				this.token = pass;
			provider = new Jp2pContainerProvider( introduction, token );
			this.provider.setContainer( container );
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

/**
 * The content provider that offers the context as a declarative service
 * @author Kees
 *
 */
class Jp2pContainerProvider extends AbstractProvider<String, Object, Jp2pServiceContainer> {

	private Jp2pServiceContainer  container;
	
	Jp2pContainerProvider() {
		super( new Palaver());
	}

	Jp2pContainerProvider( String introduction, String token ) {
		super( new Palaver( introduction, token ));
	}

	/**
	 * Get the container
	 * @return
	 */
	Jp2pServiceContainer getContainer() {
		return container;
	}

	/**
	 * Add a container and 
	 * @param container
	 */
	public void setContainer(Jp2pServiceContainer  container) {
		if( container == null )
			throw new NullPointerException();
		this.container = container;
		super.setIdentifier( container.getIdentifier());
		super.provide(container);
	}

	@Override
	protected void onDataReceived( Object msg ){
		if(!( msg instanceof String ))
			return;
		if( this.container != null )
			super.provide(container);
	}	
}

/**
 * The palaver contains the conditions for attendees to create an assembly. In this case, the attendees must
 * pass a string identifier (the package id) and provide a token that is equal
 * @author Kees
 *
 */
class Palaver extends AbstractPalaver<String>{

	private String providedToken;
	
	protected Palaver() {
		super( Jp2pDSComponent.S_IJP2P_CONTAINER_PACKAGE_ID);
	}

	protected Palaver( String introduction, String token ) {
		super(  introduction );
		this.providedToken = token;
	}

	@Override
	public String giveToken() {
		if( providedToken == null )
			return Jp2pDSComponent.S_IP2P_TOKEN;
		return providedToken;
	}

	@Override
	public boolean confirm(Object token) {
		boolean retval = false;
		if( providedToken == null )
			retval = Jp2pDSComponent.S_IP2P_TOKEN .equals( token );
		else
			retval = providedToken.equals(token);
		return retval;
	}	
}
