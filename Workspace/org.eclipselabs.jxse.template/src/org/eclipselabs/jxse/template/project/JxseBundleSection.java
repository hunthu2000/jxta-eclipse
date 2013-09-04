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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.preferences.properties.JxseContextPropertySource;
import net.osgi.jxse.service.xml.JxseXmlBuilder;
import net.osgi.jxse.utils.io.IOUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.internal.core.ibundle.IBundle;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModelBase;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.OptionTemplateWizardPage;
import org.eclipse.pde.ui.templates.PluginReference;
import org.eclipselabs.jxse.template.Activator;
import org.eclipselabs.jxse.template.TemplateUtil;

/**
 * @author Marine
 *
 */
@SuppressWarnings("restriction")
public class JxseBundleSection extends OptionTemplateSection {

	public static final String TEMPLATE_ROOT = "jxse";

	private static final String S_MSG_JXSE_CONTEXT_PROPS = "JXSE Context Properties";
	private static final String S_MSG_SET_JXSE_CONTEXT_PROPS = "Set JXSE Context Properties";
	
	// key for input field
	public static final String KEY_JXSE_CLASS_NAME = "jxseClassName";
	// key for hidden field
	public static final String KEY_DOLLAR_MARK            = "dollarMark";
	public static final String KEY_PACKAGE_PATH           = "packagePath";
	public static final String KEY_SOURCE_PATH            = "sourcePath";
	
	public static final String KEY_APPLICATION_DOMAIN = "applicationDomain";
	public static final String KEY_WEBSITE = "website";
	public static final String KEY_DOMAIN = "domain";
	
	public static final String KEY_JXSE_CONTEXT = "JxseContext";
	public static final String FILE_JXSE_XML = "JXSE-INF/jxse.xml";
	public static final String FILE_OSGI_XML = "OSGI-INF/attendees.xml";
	public static final String FOLDER_OSGI = "OSGI-INF/";

	public static final String JXSE_NET_OSGI_JXSE = "net.osgi.jxse";
	public static final String JXSE_NET_OSGI_JXSE_SERVICE = JXSE_NET_OSGI_JXSE + ".service";
	public static final String ORG_ECLIPSELABS_OSGI_BROKER = "org.eclipselabs.osgi.ds.broker";

	public static final String S_META_INF = "META-INF/";
	public static final String S_MANIFEST_MF = "MANIFEST.MF";
	public static final String S_JXSE_INF = "JXSE-INF/";
	public static final String S_JXSE_FILE = "jxse.xml";

	public static final String S_OSGI_INF = "OSGI-INF/";
	public static final String S_ATTENDESS_XML = "attendees.xml";

	private final String DS_MANIFEST_KEY = "Service-Component"; //$NON-NLS-1$

	private String packageName             = null;
	private String packagePath             = null;
	private String dollarMark              = null;
	private String sourcePath              = null;
	
	private String domain                  = null;
	private String website                 = null;
	private String applicationDomain       = null;
	
	private String pluginName;
	
	private ContextWizardOption view;
	
	private Logger logger = Logger.getLogger( JxseBundleSection.class.getName() );

	public JxseBundleSection() {
		super();
		this.setPageCount(1);
		this.createOptions();
	}

	public void createOptions() {
		view = new ContextWizardOption( this, S_MSG_JXSE_CONTEXT_PROPS, S_MSG_SET_JXSE_CONTEXT_PROPS );
		this.registerOption(view, null,  0 );
	}
	
	
	@Override
	protected void initializeFields(IFieldData data) {
		String id = data.getId();
		this.packageName = TemplateUtil.getFormattedPackageName(id);
		this.packagePath = packageName.replace('.', '\\');
		this.dollarMark = "$";
		this.sourcePath = "";
		this.pluginName = data.getId();
		
		initializeOption(KEY_PACKAGE_PATH, packagePath);
		initializeOption(KEY_DOLLAR_MARK, this.dollarMark);
		initializeOption(KEY_SOURCE_PATH, this.sourcePath);
		
		if( view != null ){
			view.setPlugin_id( data.getId() );	
			view.setIdentifier( data.getName());
			view.init();
		}
	}

	public String getPluginName() {
		return pluginName;
	}

	@Override
	public void addPages(Wizard wizard) {
		WizardPage page = ( OptionTemplateWizardPage )this.createPage(0, "jxse_bundle_context_id_2");
		page.setTitle("JXSE Configuration Page (2)");
		page.setDescription("This page is used to configure the application");
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
		return TEMPLATE_ROOT;
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
		return new String[]{FOLDER_OSGI, FILE_OSGI_XML, FILE_JXSE_XML};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.ITemplateSection#getUsedExtensionPoint()
	 */
	@Override
	public String getUsedExtensionPoint() {
		return null;
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
	public IPluginReference[] getDependencies(String schemaVersion) {
		ArrayList<PluginReference> result = new ArrayList<PluginReference>();

		result.add(new PluginReference( JXSE_NET_OSGI_JXSE, null, 0)); //$NON-NLS-1$
		result.add(new PluginReference( JXSE_NET_OSGI_JXSE_SERVICE, null, 0)); //$NON-NLS-1$
		result.add(new PluginReference( ORG_ECLIPSELABS_OSGI_BROKER, null, 0)); //$NON-NLS-1$
		return result.toArray(
				new IPluginReference[result.size()]);
	}

	
	@Override
	protected void generateFiles(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.generateFiles(monitor);
	}

	@Override
	protected void generateFiles(IProgressMonitor monitor, URL locationUrl)
			throws CoreException {
		// TODO Auto-generated method stub
		super.generateFiles(monitor, locationUrl);
	}

	@Override
	protected void updateModel(IProgressMonitor monitor) throws CoreException {
		logger.info("Updating model");
		try {
			view.complete();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		IBundlePluginModelBase mb = (IBundlePluginModelBase) model;
		IBundle bundle = mb.getBundleModel().getBundle();
		bundle.setHeader( DS_MANIFEST_KEY, FILE_OSGI_XML );
		createComponent(monitor);
	}
	
	
	private void createComponent( IProgressMonitor monitor ){
		JxseXmlBuilder<ContextProperties, ContextDirectives> builder = 
				new JxseXmlBuilder<ContextProperties, ContextDirectives>();
		InputStream source = null;
		JxseContextPropertySource ps = this.view.getPropertySource();
		try{
			source = new ByteArrayInputStream( builder.build( ps ).getBytes()); 
			this.createFile(project, S_JXSE_INF + "/", S_JXSE_FILE, source, monitor);
			IOUtils.closeInputStream(source);
			monitor.worked(3);
			source = new ByteArrayInputStream( builder.build( ps ).getBytes()); 
			this.createFile(project, S_OSGI_INF + "/", S_ATTENDESS_XML, source, monitor);
			monitor.worked(4);
		}
		finally{
			IOUtils.closeInputStream(source);
		}
		
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE,  monitor );
		} catch (CoreException e) {
			logger.severe( e.getMessage() );
			e.printStackTrace();
		}

	}

	/**
	 * Create the given file from the inputstream
	 * @param project
	 * @param directory
	 * @param name
	 * @param source
	 * @param monitor
	 */
	protected void createFile( IProject project, String directory, String name, InputStream source, IProgressMonitor monitor ){
		IFolder folder = project.getFolder( directory );
		if( !folder.exists() ){
			try {
				folder.create(true, true, monitor);
				IFile file = project.getFile(directory + name );
				file.create(source, true, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}finally{
				IOUtils.closeInputStream( source);
			}
		}
	}

}
