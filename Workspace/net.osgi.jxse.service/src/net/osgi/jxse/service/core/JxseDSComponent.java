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

import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseServiceContext;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractProvider;


public class JxseDSComponent extends AbstractAttendeeProviderComponent {

	public static final String S_IJXSE_CONTAINER_PACKAGE_ID = "org.osgi.jxse.service.core";
	public static final String S_IJXSE_TOKEN = "org.osgi.jxse.token";
	
	private JxseContextProvider provider;
	private String introduction;
	private String token;

	protected JxseDSComponent() {}

	protected JxseDSComponent( AbstractJxseBundleActivator activator ) {
		this( S_IJXSE_CONTAINER_PACKAGE_ID, S_IJXSE_TOKEN, activator);
	}

	protected JxseDSComponent( String introduction, String token, AbstractJxseBundleActivator activator ) {
		this.token = token;
		this.introduction = introduction;
		this.setActivator(activator);
	}

	protected JxseServiceContext getContext() {
		return provider.getContainer();
	}

	private final void setActivator(AbstractJxseBundleActivator activator) {
		try{
			JxseServiceContext context = activator.getServiceContext();
			String pass = (String) context.getProperty( ContextProperties.PASS_1);
			if( !Utils.isNull( pass ))
				this.introduction = pass;
			pass = (String) context.getProperty( ContextProperties.PASS_2);
			if( !Utils.isNull( pass ))
				this.token = pass;
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
class JxseContextProvider extends AbstractProvider<String, Object, JxseServiceContext> {

	private JxseServiceContext  container;
	
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
	JxseServiceContext getContainer() {
		return container;
	}

	/**
	 * Add a container and 
	 * @param container
	 */
	public void setContainer(JxseServiceContext  container) {
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
		super( JxseDSComponent.S_IJXSE_CONTAINER_PACKAGE_ID);
	}

	protected Palaver( String introduction, String token ) {
		super(  introduction );
		this.providedToken = token;
	}

	@Override
	public String giveToken() {
		if( providedToken == null )
			return JxseDSComponent.S_IJXSE_TOKEN;
		return providedToken;
	}

	@Override
	public boolean confirm(Object token) {
		boolean retval = false;
		if( providedToken == null )
			retval = JxseDSComponent.S_IJXSE_TOKEN .equals( token );
		else
			retval = providedToken.equals(token);
		return retval;
	}	
}
