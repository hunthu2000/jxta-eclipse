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
package net.osgi.jp2p.log;

import net.osgi.jp2p.component.Jp2pComponent;

public class LoggerComponent extends Jp2pComponent<LoggerPropertySource>{

	public LoggerComponent( LoggerFactory factory ) {
		super( (LoggerPropertySource) factory.getPropertySource() );
	}
}