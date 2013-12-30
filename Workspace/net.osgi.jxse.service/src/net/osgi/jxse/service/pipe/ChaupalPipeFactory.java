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

import net.jxta.pipe.PipeService;
import net.osgi.jxse.builder.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.pipe.PipeServiceFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class ChaupalPipeFactory extends PipeServiceFactory{

	@SuppressWarnings("unchecked")
	public ChaupalPipeFactory( BuilderContainer container, PipeServiceFactory factory ) {
		super( container,  (IJxsePropertySource<IJxseProperties>) factory.getPropertySource().getParent() );
		super.setSource(factory.createPropertySource());
	}

	@Override
	protected ChaupalPipeService onCreateComponent( IJxsePropertySource<IJxseProperties> source) {
		IJxseComponent<PipeService> ds = super.onCreateComponent( source );
		ChaupalPipeService service = new ChaupalPipeService( (IJxseWritePropertySource<IJxseProperties>) source, ds.getModule() );
		return service;
	}
}