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

import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.preferences.JxsePreferences;
import net.osgi.jxse.preferences.properties.JxseContextPropertySource;

import org.eclipse.pde.ui.templates.BaseOptionTemplateSection;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;

public class ContextWizardView extends TemplateOption implements IPropertySourceView {
	
	private Text text_identifier;
	private Label lbl_plugin_id;
	private Text text_home_folder;
	private Combo combo;
	
	private Button btnAutoStart;
	private Combo combo_peer_id;
	private Button btnGenerate;
	private Button btnPersist;
	
	private String plugin_id, identifier;
	
	private JxseContextPropertySource ps;
	private Label lblPort;
	private Spinner spinner;

	/**
	 * @wbp.parser.constructor
	 * @wbp.parser.entryPoint
	 */
	public ContextWizardView( BaseOptionTemplateSection section, String name, String label) {
		super( section, name, label);
	}

	/* (non-Javadoc)
	 * @see org.eclipselabs.jxse.template.project.IPropertySourceView#getPs()
	 */
	@Override
	public JxseContextPropertySource getPropertySource() {
		return ps;
	}

	@Override
	public void createControl(Composite parent, int span ) {
		Composite container = parent;
		container.setLayout(new GridLayout(2, false));
				
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Plugin-Id:");
		
		lbl_plugin_id = new Label(container, SWT.BORDER);
		lbl_plugin_id.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Identifier:");
		
		text_identifier = new Text(container, SWT.BORDER);
		text_identifier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label lblHomeFolder = new Label(container, SWT.NONE);
		lblHomeFolder.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHomeFolder.setText("Home Folder:");
		
		text_home_folder = new Text(container, SWT.BORDER);
		text_home_folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Label lblConfigMode = new Label(container, SWT.NONE);
		lblConfigMode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConfigMode.setText("Config Mode:");
		
		combo = new Combo(container, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblPort = new Label(container, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPort.setText("Port:");
		
		spinner = new Spinner(container, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		btnAutoStart = new Button(container, SWT.CHECK);
		btnAutoStart.setText("Auto Start");
		new Label(container, SWT.NONE);
		
		Group grpPeerId = new Group(container, SWT.NONE);
		grpPeerId.setText("Peer ID:");
		grpPeerId.setLayout(new GridLayout(1, false));
		grpPeerId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		combo_peer_id = new Combo(grpPeerId, SWT.NONE);
		combo_peer_id.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnGenerate = new Button(grpPeerId, SWT.CHECK);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button selected = (Button) e.getSource();
				combo_peer_id.setEnabled(!selected.getSelection());
			}
		});
		btnGenerate.setText("Generate");
		
		btnPersist = new Button(grpPeerId, SWT.CHECK);
		btnPersist.setText("Persist");
	}
	
	public void init(){
		ps = new JxseContextPropertySource( this.plugin_id, this.identifier);
		Object obj = ps.getDefault( ContextProperties.HOME_FOLDER ).toString();
		if( obj != null )
			this.text_home_folder.setText( obj.toString() );
		obj = ps.getDefault( ContextProperties.PLUGIN_ID );
		if( obj != null )
			this.lbl_plugin_id.setText( " " + obj.toString() );
		obj = ps.getDefault( ContextProperties.IDENTIFIER );
		if( obj != null )
			this.text_identifier.setText( obj.toString() );
		obj = ps.getDefault( ContextProperties.PEER_ID );
		this.combo.setItems( JxsePreferences.getConfigModes());
		this.combo.select(ConfigMode.EDGE.ordinal());
		this.spinner.setMinimum( JxseContextPropertySource.DEF_MIN_PORT );
		this.spinner.setMaximum( JxseContextPropertySource.DEF_MAX_PORT );
		this.spinner.setSelection( (int) ps.getDefault( ContextProperties.PORT ));

		this.btnAutoStart.setSelection((boolean) ps.getDefaultDirectives( ContextDirectives.AUTO_START ));
		this.btnPersist.setSelection((boolean) ps.getDefaultDirectives( ContextDirectives.PEER_ID_PERSIST ));
		boolean create = (boolean) ps.getDefaultDirectives( ContextDirectives.PEER_ID_CREATE );
		this.btnGenerate.setSelection(create);
		this.combo_peer_id.setEnabled(!create);
	}

	public void setPlugin_id(String plugin_id) {
		this.plugin_id = plugin_id;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}	
	
	/**
	 * Complete the view by filling in the properties and directives
	 */
	public boolean complete() throws Exception{
		if( !ps.setProperty( ContextProperties.HOME_FOLDER, this.text_home_folder.getText() ))
			return false;
		if( !ps.setProperty( ContextProperties.PLUGIN_ID, this.lbl_plugin_id.getText() ))
			return false;
		if( !ps.setProperty( ContextProperties.IDENTIFIER, this.text_identifier.getText() ))
			return false;
		if( !ps.setProperty( ContextProperties.CONFIG_MODE, this.combo.getText() ))
			return false;
		if( !ps.setProperty( ContextProperties.PORT, this.spinner.getText() ))
			return false;
		if( !ps.setDirective( ContextDirectives.AUTO_START, this.btnAutoStart.getSelection() ))
			return false;
		if( !ps.setDirective( ContextDirectives.PEER_ID_CREATE, this.btnGenerate.getSelection() ))
			return false;
		return ps.setDirective( ContextDirectives.PEER_ID_PERSIST, this.btnPersist.getSelection() );
	}
}