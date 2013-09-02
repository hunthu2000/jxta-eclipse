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
	public boolean performFinish() {
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}	
}
