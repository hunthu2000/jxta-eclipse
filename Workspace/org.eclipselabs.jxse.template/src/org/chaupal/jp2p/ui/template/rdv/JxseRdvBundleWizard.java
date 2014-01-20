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
/**
 * 
 */
package org.chaupal.jp2p.ui.template.rdv;

import org.chaupal.jp2p.ui.template.config.JxseBundleWizard;
import org.eclipse.pde.ui.templates.ITemplateSection;

/**
 * @author Marine
 *
 */
public class JxseRdvBundleWizard extends JxseBundleWizard{

	@Override
	public ITemplateSection[] createTemplateSections() {
		ITemplateSection[] sections = new ITemplateSection[1];
		RdvTemplateSection acs1 = new RdvTemplateSection();
		sections[0] = acs1;
		return sections;
	}
}
