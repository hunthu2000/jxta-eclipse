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
package org.eclipselabs.jxse.template.project;

import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.utils.StringStyler;

import org.eclipse.pde.ui.templates.BaseOptionTemplateSection;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.widgets.Composite;

public class ContextWizardOption extends TemplateOption implements IPropertySourceView{
	
	public enum TemplateOptions{
		CUSTOM,
		SIMPLE_NETWORK_MANAGER;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
		
		public static String[] getValues(){
			String[] retval = new String[ values().length ];
			for( int i=0; i< values().length; i++ )
				retval[i] = values()[i].toString();
			return retval;
		}
	}
	private ContextView view;

	public ContextWizardOption( JxseBundleSection section, String name, String label) {
		super( section, name, label);
	}

	/* (non-Javadoc)
	 * @see org.eclipselabs.jxse.template.project.IPropertySourceView#getPs()
	 */
	@Override
	public JxseContextPropertySource getPropertySource() {
		return this.view.getPropertySource();
	}

	public void setPlugin_id(String plugin_id) {
		this.view.setPlugin_id(plugin_id);
	}

	public void setIdentifier(String identifier) {
		this.view.setIdentifier(identifier);
	}	

	/**
	 * Get the template that is requested
	 * @return
	 */
	public TemplateOptions getTemplate(){
		return this.view.getTemplate();
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent, int span ) {
		view = new ContextView( parent, span );
		JxseBundleSection section = (JxseBundleSection) super.getSection();
		//view.addTemplateOptionListener( );
	}

	public void init(){
		this.view.init();
	}
	
	/**
	 * Determines the flow of the wizard
	 * @return
	 */
	public TemplateOptions getTemplateOption(){
		return view.getSelectedOption();
	}
	
	@Override
	public boolean complete() throws Exception {
		return view.complete();
	}
}