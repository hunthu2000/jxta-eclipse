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
package net.osgi.jxse.factory;

public abstract class AbstractFactoryConvertor<T, U extends Object> extends
		AbstractServiceComponentFactory<U> {
 
	private IComponentFactory<T> factory;
	
	public AbstractFactoryConvertor( IComponentFactory<T> factory ){
		super( factory.getComponentName() );
		this.factory = factory;
	}

	public AbstractFactoryConvertor( Components componentName, IComponentFactory<T> factory ){
		super( componentName );
		this.factory = factory;
	}

	protected IComponentFactory<T> getFactory() {
		return factory;
	}

	@Override
	protected void fillDefaultValues() {
		// USUALLY NOT NEEDED, SO WE IMPLEMENT A DEFAULT
	}
}
