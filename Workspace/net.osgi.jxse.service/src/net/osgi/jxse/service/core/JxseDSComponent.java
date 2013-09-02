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
package net.osgi.jxse.service.core;

import net.osgi.jxse.service.activator.AbstractJxseBundleActivator;
import net.osgi.jxse.utils.Utils;

import org.eclipselabs.osgi.ds.broker.service.AbstractAttendeeProviderComponent;

import net.jxta.platform.NetworkManager;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.factory.IComponentFactory.Directives;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractProvider;


public class JxseDSComponent extends AbstractAttendeeProviderComponent {

	static final String S_IJXTACONTAINER_PACKAGE_ID = "org.osgi.jxta.service.ijxtaservicecomponent";
	static final String S_IJXTA_TOKEN = "org.osgi.jxta.token";
	
	private JxseContextProvider provider;
	private String introduction;
	private String token;

	protected JxseDSComponent() {}

	protected JxseDSComponent( AbstractJxseBundleActivator activator ) {
		this.setActivator(activator);
	}

	protected JxseDSComponent( String introduction, String token, AbstractJxseBundleActivator activator ) {
		this.token = token;
		this.introduction = introduction;
		this.setActivator(activator);
	}

	protected IJxseServiceContext<NetworkManager> getContext() {
		return provider.getContainer();
	}

	private final void setActivator(AbstractJxseBundleActivator activator) {
		try{
			IJxseServiceContext<NetworkManager> context = activator.getServiceContext();
			if( Utils.isNull( this.introduction))
					this.introduction = (String) context.getProperty( Directives.PASS1);
			if( Utils.isNull( this.token ))
				this.token = (String) context.getProperty( Directives.PASS2);
			provider = new JxseContextProvider( introduction, token );
			this.provider.setContainer( context );
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
class JxseContextProvider extends AbstractProvider<String, Object, IJxseServiceContext<NetworkManager> > {

	private IJxseServiceContext<NetworkManager>  container;
	
	JxseContextProvider() {
		super( new Palaver());
	}

	JxseContextProvider( String introduction, String token ) {
		super( new Palaver( introduction, token ));
	}

	/**
	 * Get the container
	 * @return
	 */
	IJxseServiceContext<NetworkManager> getContainer() {
		return container;
	}

	/**
	 * Add a container and 
	 * @param container
	 */
	public void setContainer(IJxseServiceContext<NetworkManager>  container) {
		if( container == null )
			throw new NullPointerException();
		this.container = container;
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
		super( JxseDSComponent.S_IJXTACONTAINER_PACKAGE_ID);
	}

	protected Palaver( String introduction, String token ) {
		super(  introduction );
		this.providedToken = token;
	}

	@Override
	public String giveToken() {
		if( providedToken == null )
			return JxseDSComponent.S_IJXTA_TOKEN;
		return providedToken;
	}

	@Override
	public boolean confirm(Object token) {
		boolean retval = false;
		if( providedToken == null )
			retval = JxseDSComponent.S_IJXTA_TOKEN .equals( token );
		else
			retval = providedToken.equals(token);
		return retval;
	}	
}
