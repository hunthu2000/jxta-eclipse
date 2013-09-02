/**
 * 
 */
package org.eclipselabs.jxse.template.sections;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipselabs.jxse.template.Activator;
import org.eclipselabs.jxse.template.TemplateUtil;

/**
 * @author Kees Pieters
 *
 */
public class JxseContextSection extends OptionTemplateSection {

	public static final String TEMOPLATE_ROOT = "jxse";
	
	// key for hidden field
	public static final String KEY_DOLLAR_MARK            = "dollarMark"; //$NON-NLS-1$
	public static final String KEY_PACKAGE_PATH           = "packagePath"; //$NON-NLS-1$
	public static final String KEY_SOURCE_PATH            = "sourcePath"; //$NON-NLS-1$
	
	public static final String KEY_APPLICATION = "application"; //$NON-NLS-1$
	public static final String KEY_VENDOR = Messages.JxseContextSection1_0;
	public static final String KEY_AUTHOR = Messages.JxseContextSection1_1;
	
	
	private String packageName             = null;
	private String packagePath             = null;
	private String dollarMark              = null;
	private String sourcePath              = null;
	
	public JxseContextSection() {
		super();
		this.setPageCount(1);
		createOptions();
	}
	
	private void createOptions() {
		TemplateOption classOption = addOption(KEY_APPLICATION,	Messages.JxseContextSection1_2, Messages.JxseContextSection1_3, 0);
		classOption.setRequired(true);
		classOption = addOption(KEY_AUTHOR, Messages.JxseContextSection1_11, Messages.JxseContextSection1_12, 0);
		classOption.setRequired(true);
	}
	
	@Override
	protected void initializeFields(IFieldData data) {
		String id = data.getId();
		this.packageName = TemplateUtil.getFormattedPackageName(id);
		this.packagePath = packageName.replace('.', '\\');
		this.dollarMark = "$"; //$NON-NLS-1$
		this.sourcePath = ""; //$NON-NLS-1$
		
		initializeOption(KEY_PACKAGE_PATH, packagePath);
		initializeOption(KEY_DOLLAR_MARK, this.dollarMark);
		initializeOption(KEY_SOURCE_PATH, this.sourcePath);
	}

	
	@Override
	public WizardPage createPage(int pageIndex, String helpContextId) {
		// TODO Auto-generated method stub
		return super.createPage(pageIndex, helpContextId);
	}

	@Override
	public void addPages(Wizard wizard) {
		WizardPage page = this.createPage(0, "jxse_section_help_id"); //$NON-NLS-1$
		page.setTitle(Messages.JxseContextSection1_16);
		page.setDescription(Messages.JxseContextSection1_17);
		wizard.addPage(page);
		this.markPagesAdded();
	}

	@Override
	public String getStringOption(String name) {
		if (name.equals(KEY_PACKAGE_NAME))
			return packageName;
		return super.getStringOption(name);
	}

	@Override
	public int getNumberOfWorkUnits() {
		return super.getNumberOfWorkUnits() + 1;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getInstallURL()
	 */
	@Override
	protected URL getInstallURL() {
		return Activator.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getSectionId()
	 */
	@Override
	public String getSectionId() {
		return TEMOPLATE_ROOT;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#getPluginResourceBundle()
	 */
	@Override
	protected ResourceBundle getPluginResourceBundle() {
		return Platform.getResourceBundle(Activator.getDefault().getBundle());
	}

	@Override
	protected void updateModel(IProgressMonitor monitor) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.ITemplateSection#getNewFiles()
	 */
	@Override
	public String[] getNewFiles() {
		return new String[0];
	}

	@Override
	public IPluginReference[] getDependencies(String schemaVersion) {
		ArrayList<PluginReference> result = new ArrayList<PluginReference>();

		result.add(new PluginReference("org.condast.util", null, 0)); //$NON-NLS-1$
		result.add(new PluginReference("org.condast.Jxse.osgi", null, 0)); //$NON-NLS-1$
        result.add(new PluginReference("org.condast.concept", null, 0)); //$NON-NLS-1$
        result.add(new PluginReference("org.condast.concept.db", null, 0)); //$NON-NLS-1$
        result.add(new PluginReference("org.condast.concept.model", null, 0)); //$NON-NLS-1$
        result.add(new PluginReference("org.condast.concept.template", null, 0)); //$NON-NLS-1$

		return result.toArray(
				new IPluginReference[result.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.ITemplateSection#getUsedExtensionPoint()
	 */
	@Override
	public String getUsedExtensionPoint() {
		return null;
	}
}
