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
package net.osgi.jp2p.pipe;

import net.jxta.pipe.PipeService;
import net.osgi.jp2p.builder.ContainerBuilder;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponentNode;
import net.osgi.jp2p.factory.AbstractPeerGroupDependencyFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public class PipeServiceFactory extends
		AbstractPeerGroupDependencyFactory<PipeService> {

	public PipeServiceFactory( ContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent );
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}

	@Override
	protected PipePropertySource onCreatePropertySource() {
		return new PipePropertySource( super.getParentSource() );
	}

	@Override
	protected IJp2pComponent<PipeService> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		return new Jp2pComponentNode<PipeService>( super.getPeerGroup().getPipeService());
	}
}