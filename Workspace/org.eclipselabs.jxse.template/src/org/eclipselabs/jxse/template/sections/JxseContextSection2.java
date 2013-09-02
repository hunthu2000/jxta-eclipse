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
package org.eclipselabs.jxse.template.sections;

import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.OptionTemplateWizardPage;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipselabs.jxse.template.Activator;
import org.eclipselabs.jxse.template.TemplateUtil;

/**
 * @author Marine
 *
 */
public class JxseContextSection2 extends OptionTemplateSection {

	// key for input field
	public static final String KEY_AIEONF_CLASS_NAME = "aieonfClassName";
	// key for hidden field
	public static final String KEY_DOLLAR_MARK            = "dollarMark";
	public static final String KEY_PACKAGE_PATH           = "packagePath";
	public static final String KEY_SOURCE_PATH            = "sourcePath";
	
	public static final String KEY_APPLICATION_DOMAIN = "applicationDomain";
	public static final String KEY_WEBSITE = "website";
	public static final String KEY_DOMAIN = "domain";
	
	
	private String packageName             = null;
	private String packagePath             = null;
	private String dollarMark              = null;
	private String sourcePath              = null;
	
	private String domain                  = null;
	private String website                 = null;
	private String applicationDomain       = null;
	
	private OptionTemplateWizardPage page;

	public JxseContextSection2() {
		super();
		this.setPageCount(2);
		this.createOptions();
	}

	public void createOptions() {
		TemplateOption classOption = addOption(KEY_DOMAIN, "Domain", "Domain", 0);
		classOption.setRequired(true);
		classOption = addOption(KEY_APPLICATION_DOMAIN, "Application Domain", "AppDomain", 1);
		classOption.setRequired(true);
		classOption = addOption(KEY_WEBSITE, "Website", "www.your-webiste.org", 1);
		classOption.setRequired(true);
	}

	public void createOptions( String domain, String organisation) {
		TemplateOption classOption;
		if(( domain != null ) && ( domain.trim().length() > 0 )){
			classOption = addOption(KEY_DOMAIN, domain, "Domain", 0);
			classOption.setRequired(true);
			classOption = addOption(KEY_APPLICATION_DOMAIN, domain, "AppDomain", 1);
			classOption.setRequired(true);
		}
		if(( organisation != null ) && ( organisation.trim().length() > 0 )){
			classOption = addOption(KEY_WEBSITE, "Website", "www"+ organisation.trim().toLowerCase() + "com", 1);
			classOption.setRequired(true);
		}
		if(( page != null ) && ( page.getControl() != null ))
 			page.setVisible(true);
	}
	
	
	@Override
	protected void initializeFields(IFieldData data) {
		String id = data.getId();
		this.packageName = TemplateUtil.getFormattedPackageName(id);
		this.packagePath = packageName.replace('.', '\\');
		this.dollarMark = "$";
		this.sourcePath = "";
		
		initializeOption(KEY_PACKAGE_PATH, packagePath);
		initializeOption(KEY_DOLLAR_MARK, this.dollarMark);
		initializeOption(KEY_SOURCE_PATH, this.sourcePath);
	}
	
	@Override
	public void addPages(Wizard wizard) {
		page = ( OptionTemplateWizardPage )this.createPage(1, "aieonf_section_context_id_2");
		page.setTitle("AieonF Configuration Page (2)");
		page.setDescription("This page is used to configurate the application");
		wizard.addPage(page);
		this.markPagesAdded();
	}
	
	@Override
	public String getStringOption(String name) {
		if (name.equals(KEY_PACKAGE_NAME)) {
			return packageName;
		} else if (name.equals(KEY_PACKAGE_PATH)) {
			return packagePath;
		} else if (name.equals(KEY_DOLLAR_MARK)) {
			return dollarMark;
		} else if (name.equals(KEY_SOURCE_PATH)) {
			return sourcePath;
		} else if (name.equals(KEY_APPLICATION_DOMAIN)) {
			return this.applicationDomain;
		} else if (name.equals(KEY_DOMAIN)) {
			return this.domain;
		} else if (name.equals(KEY_WEBSITE)) {
			return this.website;
		}
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
		return Activator.getDefault().getBundle().getEntry("/");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.OptionTemplateSection#getSectionId()
	 */
	@Override
	public String getSectionId() {
		return "aieonf_2";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#getPluginResourceBundle()
	 */
	@Override
	protected ResourceBundle getPluginResourceBundle() {
		return Platform.getResourceBundle(Activator.getDefault().getBundle());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.ITemplateSection#getNewFiles()
	 */
	@Override
	public String[] getNewFiles() {
		return new String[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.ITemplateSection#getUsedExtensionPoint()
	 */
	@Override
	public String getUsedExtensionPoint() {
		return "org.eclipse.ui.views";
	}

	@Override
	public boolean isDependentOnParentWizard() {
		return true;
	}

	/**
	 * @param sourcePath the sourcePath to set
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	@Override
	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}
}
