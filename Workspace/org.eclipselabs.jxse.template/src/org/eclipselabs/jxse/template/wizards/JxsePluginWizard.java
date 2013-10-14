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
package org.eclipselabs.jxse.template.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Marine
 *
 */
public class JxsePluginWizard extends Wizard implements IWorkbenchWizard{

	public static final String S_MSG_SETUP_CONTEXT = "Set up JXSE Bundle Project";
	public static final String S_MSG_BUNDLE_CONTEXT_PAGE = "Jxse Bundle context";
	
	//private SelectStringsWizardPage selectStringsPage;

	public void addPages() {
		setWindowTitle( S_MSG_SETUP_CONTEXT);
		//contextPage = new ContextWizardView( S_MSG_BUNDLE_CONTEXT_PAGE);
		//addPage(contextPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}	

	
	@Override
	public boolean performFinish() {
		return false;
	}
}
