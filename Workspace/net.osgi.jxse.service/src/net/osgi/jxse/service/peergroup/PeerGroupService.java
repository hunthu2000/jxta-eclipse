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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.service.core.AbstractJxtaService;
import net.osgi.jxse.service.peergroup.IPeerGroupProperties.PeerGroupProperties;

public class PeerGroupService extends AbstractJxtaService<PeerGroup, PeerGroupProperties, IJxseDirectives.Directives>{

	public static final String S_PEERGROUP = "Jxta PeerGroup";

	public PeerGroupService(PeerGroup component ) {
		super( component );
	}

	@Override
	protected void fillDefaultValues() {
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
	public Iterator<?> iterator() {
		List<PeerGroupProperties> set = Arrays.asList(PeerGroupProperties.values());
		return set.iterator();
	}

	@Override
	protected void deactivate() {
		// TODO Auto-generated method stub
		
	}
}