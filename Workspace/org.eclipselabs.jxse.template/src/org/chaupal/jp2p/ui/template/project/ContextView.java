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
package org.chaupal.jp2p.ui.template.project;

import java.util.ArrayList;
import java.util.Collection;

import net.osgi.jp2p.container.Jp2pContainerPropertySource;

import org.chaupal.jp2p.ui.container.ContainerComposite;
import org.chaupal.jp2p.ui.template.project.ContextWizardOption.TemplateOptions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Group;

public class ContextView extends ContainerComposite {
	
	private Combo combo_template;
	private Group grpTemplate;
	
	private Collection<ITemplateOptionListener> listeners;
	
	public ContextView(Composite parent, int span) {
		super( parent, span);
		listeners = new ArrayList<ITemplateOptionListener>();
	}

	public void addTemplateOptionListener( ITemplateOptionListener listener ){
		this.listeners.add( listener );
	}

	public void removeTemplateOptionListener( ITemplateOptionListener listener ){
		this.listeners.remove( listener );
	}

	protected void notifyOptionChanged( TemplateOptions option ){
		for( ITemplateOptionListener listener: listeners )
			listener.notifyTemplateOptionChanged( new TemplateOptionEvent(this, option));
	}
	
	protected void createControl(Composite parent, int span ) {
		super.createControl(parent, span);
		grpTemplate = new Group(this, SWT.NONE);
		grpTemplate.setText("Template:");
		grpTemplate.setLayout(new GridLayout(1, false));
		grpTemplate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		combo_template = new Combo(grpTemplate, SWT.NONE);
		combo_template.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyOptionChanged( TemplateOptions.values()[(( Combo)e.widget ).getSelectionIndex() ]);
			}
		});
		combo_template.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}
	
	public void init( Jp2pContainerPropertySource properties ){
		super.init( properties );		
		this.combo_template.setItems( TemplateOptions.getValues() );
		this.combo_template.select(1);
	}

	/**
	 * Get the template that is requested
	 * @return
	 */
	public TemplateOptions getTemplate(){
		if( this.combo_template == null )
			return TemplateOptions.SIMPLE_NETWORK_MANAGER;
		else
			return TemplateOptions.values()[ this.combo_template.getSelectionIndex() ];
	}
	
	/**
	 * Set the template option
	 * @param option
	 */
	public void setTemplateOption( TemplateOptions option ){
		this.combo_template.setText( option.toString());
	}
	
	public TemplateOptions getSelectedOption() {
		return TemplateOptions.values()[ this.combo_template.getSelectionIndex() ];
	}

	/**
	 * Complete the view by filling in the properties and directives
	 */
	public boolean complete() throws Exception{
		if(!super.complete())
			return false;
		return true;
	}
}