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
package net.jp2p.jxta.pipe;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.component.Jp2pComponentNode;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.jxta.factory.AbstractPeerGroupDependencyFactory;
import net.jp2p.jxta.pipe.PipePropertySource;
import net.jxta.pipe.PipeService;

public class PipeServiceFactory extends
		AbstractPeerGroupDependencyFactory<PipeService> {

	public PipeServiceFactory( IContainerBuilder container, IJp2pPropertySource<IJp2pProperties> parent ) {
		super( container, parent );
	}

	@Override
	protected PipePropertySource onCreatePropertySource() {
		return new PipePropertySource( super.getParentSource() );
	}

	@Override
	protected IJp2pComponent<PipeService> onCreateComponent( IJp2pPropertySource<IJp2pProperties> properties) {
		return new Jp2pComponentNode<PipeService>( super.getPropertySource(), super.getDependency().getModule().getPipeService());
	}
}