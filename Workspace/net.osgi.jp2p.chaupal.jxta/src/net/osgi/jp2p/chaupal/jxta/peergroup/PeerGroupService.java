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
package net.osgi.jp2p.chaupal.jxta.peergroup;

import java.io.IOException;

import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.osgi.jp2p.chaupal.jxta.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.component.AbstractJp2pServiceNode;
import net.osgi.jp2p.component.ComponentChangedEvent;
import net.osgi.jp2p.component.ComponentEventDispatcher;
import net.osgi.jp2p.component.IComponentChangedListener;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupDirectives;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupProperties;

public class PeerGroupService extends AbstractJp2pServiceNode<PeerGroup>{

	public static final String S_PEERGROUP = "Jxta PeerGroup";
	
	private ChaupalDiscoveryService disco;
	private PeerGroup parent;
	private PeerGroupAdvertisement pgad;
	private IComponentChangedListener listener;

	public PeerGroupService( PeerGroupPropertySource source, PeerGroupAdvertisement pgad, PeerGroup parent, ChaupalDiscoveryService disco ) {
		super( source, null );
		this.disco = disco;
		this.parent = parent;
		this.pgad = pgad;
	}

	@Override
	public boolean start() {
		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		this.listener = new IComponentChangedListener(){

			@Override
			public void notifyServiceChanged(ComponentChangedEvent event) {
				if(!( event.getSource().equals( disco )))
					return;
				switch( disco.getStatus() ){
				case COMPLETED:	
					Advertisement[] ads = disco.getAdvertisements();
					try {
						disco.getModule().publish( ads[0]);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case FAILED:
					PeerGroup peergroup = createComponent( pgad );
					setModule( peergroup);
					disco.stop();
					setStatus( Status.COMPLETED);
					break;
				default:
					break;
				}
			}

		};
		dispatcher.addServiceChangeListener(listener);;
		return super.start();
	}
	
	/**
	 * Create the final component
	 * @param advertisement
	 * @return
	 */
	protected PeerGroup createComponent( PeerGroupAdvertisement advertisement) {
		String name = (String) super.getPropertySource().getProperty( PeerGroupProperties.NAME );
		String description = (String) super.getPropertySource().getProperty( PeerGroupProperties.DESCRIPTION );
		boolean publish = PeerGroupPropertySource.getBoolean(super.getPropertySource(), PeerGroupDirectives.PUBLISH );

		try {
			PeerGroup peergroup = parent.newGroup(advertisement);
			if( publish ){
				peergroup.publishGroup(name, description);
				return peergroup;
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void putProperty( PeerGroupProperties key, Object value ){
		if( value == null )
			return;
	}

	protected void putProperty( PeerGroupProperties key, Object value, boolean skipFilled ){
		if( value == null )
			return;
	}

	public Object getProperty( PeerGroupProperties key ){
		return super.getProperty(key);
	}
	
	@Override
	protected void deactivate() {
		ComponentEventDispatcher dispatcher = ComponentEventDispatcher.getInstance();
		if( this.listener != null )
			dispatcher.removeServiceChangeListener(listener);				
	}
}