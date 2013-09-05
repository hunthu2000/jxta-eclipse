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
package org.eclipselabs.jxse.template.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.ui.wizards.plugin.PluginFieldData;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.IPluginContentWizard;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;
import org.eclipse.pde.ui.templates.TemplateOption;


/**
 * @author Marine
 *
 */
@SuppressWarnings("restriction")
public class JxseBundleWizard extends NewPluginTemplateWizard  implements IPluginContentWizard{

	public static final String S_MSG_SETUP_CONTEXT = "Set up JXSE Bundle Project";
	public static final String S_MSG_BUNDLE_CONTEXT_PAGE = "Jxse Bundle context";

	private JxseBundleSection acs1;
	
	@Override
	public void init(IFieldData data) {
		super.init(data);
		//Override creation of an activator by making the plugin simple
		if( data instanceof PluginFieldData ){
			PluginFieldData fData = (PluginFieldData) data;
			fData.setSimple(true);
		}
	}

	@Override
	public ITemplateSection[] createTemplateSections() {
		ITemplateSection[] sections = new ITemplateSection[1];
		acs1 = new JxseBundleSection();
		sections[0] = acs1;
		return sections;
	}
	
	@Override
	public boolean performFinish(final IProject project, IPluginModelBase model, IProgressMonitor monitor) {
		if( !super.performFinish(project, model, monitor))
			return false;
		try {
			ITemplateSection[] sections = super.getTemplateSections();
			monitor.beginTask("perform finish", sections.length);

			JxseBundleSection contextSection = null;

			for (int i = 0; i < sections.length; i++) {
				if (sections[i] instanceof JxseBundleSection ) {
					contextSection = (JxseBundleSection) sections[i];
				}
			}

			TemplateOption[] allOptions = contextSection.getOptions(0);
			if (allOptions != null) {
				for (int i = 0; i < allOptions.length; i++) {
					TemplateOption option = allOptions[i];
					if (option != null && option instanceof ContextWizardOption) {
						contextSection.execute(project, model, new SubProgressMonitor(
								monitor, 1));
					}
				}	
			}
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		} finally {
			monitor.done();
		}
		return true;
	}
}
