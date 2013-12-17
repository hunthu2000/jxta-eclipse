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
package net.osgi.jxse.service.pipe;

import net.osgi.jxse.factory.AbstractComponentFactory;
import net.osgi.jxse.pipe.PipeServiceFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class ChaupalPipeFactory extends
		AbstractComponentFactory<ChaupalPipeService, IJxseProperties>{

	public static final String S_PIPE_SERVICE = "JxsePipeService";

	private PipeServiceFactory factory;
	
	public ChaupalPipeFactory( PipeServiceFactory factory ) {
		super( factory.getPropertySource() );
		this.factory = factory;
	}

	@Override
	protected ChaupalPipeService onCreateModule( IJxsePropertySource<IJxseProperties> properties) {
		factory.createModule();
		ChaupalPipeService ds = new ChaupalPipeService ( factory );
		return ds;
	}
}