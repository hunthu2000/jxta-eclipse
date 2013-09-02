/**
 * 
 */
package org.eclipselabs.jxse.template.fragment;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.osgi.jxse.utils.Utils;
import net.osgi.jxse.utils.io.IOUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.ui.IPluginContentWizard;
import org.eclipse.pde.ui.templates.AbstractNewPluginTemplateWizard;
import org.eclipse.pde.ui.templates.ITemplateSection;

/**
 * @author Marine
 *
 */
public class JxseFragmentWizard extends AbstractNewPluginTemplateWizard  implements IPluginContentWizard{

	public static final String S_MSG_SETUP_FRAGMENT = "Set up JXSE Fragment Project";
	public static final String S_MSG_BUNDLE_FRAGMENT_PAGE = "Jxse Fragment";

	public static final String S_JXSE_INF = "JXSE-INF/";
	public static final String S_TOKENS_FILE = "tokens.txt";

	public static final String S_OSGI_INF = "OSGI-INF/";
	public static final String S_ATTENDESS_XML = "attendees.xml";

	public static final String S_META_INF = "META-INF/";
	public static final String S_MANIFEST_MF = "MANIFEST.MF";
	public static final String S_PRIOR_ELEMENT = "Bundle-RequiredExecutionEnvironment:";
	public static final String S_FRAGMENT_TEXT = "Fragment-Host: org.eclipselabs.jxse.ui;bundle-version=\"1.0.0\"\n";

	private FragmentWizardPage fragmentPage;

	private Executor executor;
	
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		fragmentPage = (FragmentWizardPage) page;
		return super.getNextPage(page);
	}
	
	@Override
	public boolean performFinish( final IProject project, IPluginModelBase model, final IProgressMonitor monitor) {
		if( fragmentPage == null )
			return false;
		String[] tokens = fragmentPage.complete();
		if(( tokens == null ) || ( Utils.isNull( tokens[0] )) || ( Utils.isNull( tokens[1])))
				return false;
		InputStream source = null;
		try{
			source = new ByteArrayInputStream(( tokens[0] + "\n" + tokens[1]).getBytes() );
			this.createFile(project, S_JXSE_INF + "/", S_TOKENS_FILE, source, monitor);
		}
		finally{
			IOUtils.closeInputStream(source);
		}

		boolean retval = super.performFinish(project, model, monitor);
		executor = Executors.newSingleThreadExecutor();
		executor.execute( new Runnable(){

			@Override
			public void run() {
				modifyManifest(project, S_PRIOR_ELEMENT, S_FRAGMENT_TEXT, monitor);
			}
			
		});
		return retval;
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

	/**
	 * Create the given file from the inputstream
	 * @param project
	 * @param directory
	 * @param name
	 * @param source
	 * @param monitor
	 */
	protected boolean modifyManifest( IProject project, String priorElement, String include, IProgressMonitor monitor ){
		String directory = S_META_INF;
		String name = S_MANIFEST_MF; 
		IFile file = project.getFile(directory + name );
		while( !file.exists() ){
			try{
				Thread.sleep(100);
			}
			catch( InterruptedException ex ){
				
			}
		}

		Scanner scanner = null;
		InputStream in = null;
		try {
			StringBuffer buffer = new StringBuffer();
			scanner = new Scanner( file.getContents() );
			while( scanner.hasNext()){
				String line = scanner.next();
				if( line.startsWith( priorElement ))
					buffer.append( include );
				buffer.append( line );
			}
			file.delete(true, monitor);
			in = new ByteArrayInputStream( buffer.toString().getBytes() );
			this.createFile(project, directory, name, in, monitor);
			return true;			
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}finally{
			scanner.close();
			IOUtils.closeInputStream(in);
		}
	}

	@Override
	protected void addAdditionalPages() {
		fragmentPage = new FragmentWizardPage( S_MSG_BUNDLE_FRAGMENT_PAGE);
		addPage(fragmentPage);
	}

	@Override
	public ITemplateSection[] getTemplateSections() {
		return new ITemplateSection[0];
	}
}
