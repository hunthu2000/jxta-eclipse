/**
 * 
 */
package org.eclipselabs.jxse.template.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.ui.IPluginContentWizard;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;
import org.eclipse.pde.ui.templates.TemplateOption;

/**
 * @author Marine
 *
 */
public class JxseBundleWizard extends NewPluginTemplateWizard  implements IPluginContentWizard{

	public static final String S_MSG_SETUP_CONTEXT = "Set up JXSE Bundle Project";
	public static final String S_MSG_BUNDLE_CONTEXT_PAGE = "Jxse Bundle context";

	private JxseBundleSection acs1;
	
	@Override
	public ITemplateSection[] createTemplateSections() {
		ITemplateSection[] sections = new ITemplateSection[1];
		acs1 = new JxseBundleSection();
		sections[0] = acs1;
		return sections;
	}
	
	@Override
	public boolean performFinish(final IProject project, IPluginModelBase model,
			IProgressMonitor monitor) {
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
					if (option != null && option instanceof ContextWizardView) {
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
