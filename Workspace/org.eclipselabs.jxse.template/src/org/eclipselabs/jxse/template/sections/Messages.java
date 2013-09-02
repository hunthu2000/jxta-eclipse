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
package org.eclipselabs.jxse.template.sections;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipselabs.jxse.template.sections.messages"; //$NON-NLS-1$
	public static String JxseContextSection1_0;
	public static String JxseContextSection1_1;
	public static String JxseContextSection1_11;
	public static String JxseContextSection1_12;
	public static String JxseContextSection1_16;
	public static String JxseContextSection1_17;
	public static String JxseContextSection1_2;
	public static String JxseContextSection1_3;
	public static String JxseContextSection1_4;
	public static String JxseContextSection1_5;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
