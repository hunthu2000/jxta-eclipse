/*******************************************************************************
 * Copyright (c) 2013 Condast.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Condast - initial API and implementation
 ******************************************************************************/
package $packageName$.service;

import net.osgi.jxse.service.core.JxseDSComponent;

import $packageName$.Activator;

/**
 * Makes the service container accessible for the IDE
 * @author keesp
 *
 */
public class OsgiComponent extends JxseDSComponent {

	public OsgiComponent() {
		super( Activator.getDefault() );
	}
}