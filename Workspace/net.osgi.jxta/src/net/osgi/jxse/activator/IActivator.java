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
package net.osgi.jxse.activator;

import net.osgi.jxse.utils.StringStyler;

public interface IActivator extends ISimpleActivator
{
	public static final String S_ACTIVATOR = "Activator";
	public static final String S_STATUS = "Status";
	
	
	public enum Status{
		IDLE,
		INITIALISING,
		INITIALISED,
		ACTIVATING,
		ACTIVE,
		PAUSED,
		AVAILABLE,
		SHUTTING_DOWN,
		FINALISING,
		FINALISED;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	/**
	 * Start activating. Returns true if the activator is paused, false
	 * if not, for instance when the activator is not started, or paused already
	 */
	public boolean pause();

	/**
	 * Get the status of the activator
	 * @return
	*/
	public Status getStatus();
	
	/**
	 * Supportive method gives true if the status is set to idle
	 * @return
	 */
	public boolean isIdle();

	/**
	 * Supportive method gives true if the status is set to available
	 * @return
	 */
	public boolean isAvailable();
}