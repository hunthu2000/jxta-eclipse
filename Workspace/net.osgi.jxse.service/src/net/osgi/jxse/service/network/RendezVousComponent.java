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
package net.osgi.jxse.service.network;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.log.JxseLevel;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.service.ServiceEventDispatcher;
import net.osgi.jxse.service.core.AbstractJxseService;
import net.osgi.jxse.service.network.IRendezVousComponent.RendezVousServiceProperties;

public class RendezVousComponent extends AbstractJxseService<RendezVousService, IRendezVousComponent.RendezVousServiceProperties, IJxseDirectives.Directives> implements RendezvousListener, IRendezVousComponent{

	public static final String S_RENDEZ_VOUS_SERVICE = "RendezVous Service";

	public static final String S_ERR_SERVICE_NOT_STARTED = "The RendezVous Service is not started. Please do this first";

	private ServiceEventDispatcher dispatcher;

	public RendezVousComponent( RendezVousService module ) {
		super( module );
	}

	
	/* (non-Javadoc)
	 * @see net.osgi.jxse.service.network.IRendezVousComponent#getProperty(net.osgi.jxse.service.network.RendezVousServiceComponent.RendezVousServiceProperties)
	 */
	@Override
	public Object getProperty( RendezVousServiceProperties key) {
		if( super.getModule() == null )
			return super.getProperty(key);
		RendezVousService service = super.getModule();
		switch( key ){
		case STATUS:
			return super.getStatus();
		case IS_RENDEZVOUS:
			return service.isRendezVous();
		case IS_CONNECTED_TO_RENDEZVOUS:
			return service.isConnectedToRendezVous();
		case RENDEZVOUS_STATUS:
			return service.getRendezVousStatus();
		default:
			return super.getProperty(key);
		}
	}

	/* (non-Javadoc)
	 * @see net.osgi.jxse.service.network.IRendezVousComponent#putProperty(net.osgi.jxse.service.network.RendezVousServiceComponent.RendezVousServiceProperties, java.lang.Object)
	 */
	@Override
	public void putProperty( RendezVousServiceProperties key, Object value) {
		if(( key == null ) || ( value == null ))
			return;
		if( super.getModule() == null ){
			super.putProperty(key, value );
			return;
		}
		RendezVousService service = super.getModule();
		switch( key ){
		case AUTO_START:
			service.setAutoStart( (boolean) value );
		default:
			super.putProperty(key, value );
		}
	}

	@Override
	public Iterator<?> iterator() {
		return Arrays.asList( RendezVousServiceProperties.values() ).iterator();
	}

	@Override
	protected void fillDefaultValues() {
	}

	@Override
	protected void activate() {
		dispatcher = ServiceEventDispatcher.getInstance();
		super.activate();
	}

	@Override
	protected void deactivate() {
		dispatcher = null;
		super.getModule().stopApp();
	}

	@Override
	public void rendezvousEvent(RendezvousEvent event) {
		if( dispatcher == null ){
			Logger log = Logger.getLogger( this.getClass().getName() );
			log.log( JxseLevel.JXSELEVEL, S_ERR_SERVICE_NOT_STARTED );
		}
	}
}

class RendezvousServiceFactory extends AbstractComponentFactory<RendezVousService, IRendezVousComponent.RendezVousServiceProperties, IJxseDirectives.Directives>{

	private NetPeerGroupService parent;
	
	public RendezvousServiceFactory( NetPeerGroupService parent ) {
		super( null );//Components.RENDEZVOUS_SERVICE );
		this.parent = parent;
	}

	@Override
	public RendezVousService createModule() {
		return parent.getModule().getRendezVousService();
	}

	@Override
	protected void onParseDirectivePriorToCreation(
			IJxseDirectives.Directives directive,
			Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected RendezVousService onCreateModule(
			IJxsePropertySource<RendezVousServiceProperties, IJxseDirectives.Directives> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onProperytySourceCreated(
			IJxsePropertySource<?, ?> ps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onParseDirectiveAfterCreation(
			IJxseDirectives.Directives directive,
			Object value) {
		// TODO Auto-generated method stub
		
	}
	
}
