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
package net.osgi.jxse.service.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.component.AbstractJxseService;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProperties.PeerGroupProperties;

public class PeerGroupService extends AbstractJxseService<PeerGroup>{

	public static final String S_PEERGROUP = "Jxta PeerGroup";

	public PeerGroupService(IComponentFactory<PeerGroup> factory ) {
		super( factory );
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
		// TODO Auto-generated method stub
		
	}
}