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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.preferences.AbstractJxsePreferences;
import net.osgi.jxse.utils.Utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipselabs.jxse.template.project.ContextWizardOption.TemplateOptions;

public class ContextView extends Composite {
	
	private static final String S_JXSE_CONTEXT_1 = ".jxse.context1";
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
	private Group grpSecurity;
	private Group grpTemplate;
	private Combo combo_template;
	private Label lblPass;
	private Text text_pass1;
	private Label lblPass_1;
	private Text text_pass2;
	private Label lblId;
	private Text text_id;
	
	private Collection<ITemplateOptionListener> listeners;
	
	public ContextView(Composite parent, int span) {
		super( parent, span);
		listeners = new ArrayList<ITemplateOptionListener>();
		this.createControl(parent, span);
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
	
	private void createControl(Composite parent, int span ) {
		parent.setLayout( new FillLayout());
		Composite container = this;
		container.setLayout(new GridLayout(2, false));
				
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Plugin-Id:");
		
		lbl_plugin_id = new Label(container, SWT.BORDER);
		lbl_plugin_id.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblId = new Label(this, SWT.NONE);
		lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblId.setText("id:");
		
		text_id = new Text(this, SWT.BORDER);
		text_id.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ps.setId((( Text )e.item ).getText() );
			}
		});
		text_id.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		text_identifier = new Text(container, SWT.BORDER);
		text_identifier.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ps.setProperty( ContextProperties.IDENTIFIER, (( Text )e.item ).getText() );
			}
		});
		text_identifier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label lblHomeFolder = new Label(container, SWT.NONE);
		lblHomeFolder.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHomeFolder.setText("Home Folder:");
		
		text_home_folder = new Text(container, SWT.BORDER);
		text_home_folder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ps.setProperty( ContextProperties.HOME_FOLDER, (( Text )e.item ).getText() );
			}
		});
		text_home_folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Label lblConfigMode = new Label(container, SWT.NONE);
		lblConfigMode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConfigMode.setText("Config Mode:");
		
		combo = new Combo(container, SWT.NONE);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ps.setProperty( ContextProperties.CONFIG_MODE, ConfigMode.valueOf((( Text )e.item ).getText() ));
			}
		});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblPort = new Label(container, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPort.setText("Port:");
		
		spinner = new Spinner(container, SWT.BORDER);
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ps.setProperty( ContextProperties.PORT, (( Spinner )e.item ).getSelection() );
			}
		});
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(this, SWT.NONE);
		
		btnAutoStart = new Button(container, SWT.CHECK);
		btnAutoStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ps.setDirective( ContextDirectives.AUTO_START, (( Button )e.item ).getSelection() );
			}
		});
		btnAutoStart.setText("Auto Start");
		
		Group grpPeerId = new Group(container, SWT.NONE);
		grpPeerId.setText("Peer ID:");
		grpPeerId.setLayout(new GridLayout(2, false));
		grpPeerId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		combo_peer_id = new Combo(grpPeerId, SWT.NONE);
		combo_peer_id.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		btnGenerate = new Button(grpPeerId, SWT.CHECK);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button selected = (Button) e.getSource();
				ps.setDirective( ContextDirectives.PEER_ID_CREATE, selected.getSelection() );
				combo_peer_id.setEnabled(!selected.getSelection());
			}
		});
		btnGenerate.setText("Generate");
		
		btnPersist = new Button(grpPeerId, SWT.CHECK);
		btnPersist.setText("Persist");
		btnPersist.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ps.setDirective( ContextDirectives.PEER_ID_PERSIST, (( Button )e.item ).getSelection() );
			}
		});

		grpSecurity = new Group(this, SWT.NONE);
		grpSecurity.setText("Security:");
		grpSecurity.setLayout(new GridLayout(2, false));
		grpSecurity.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		lblPass = new Label(grpSecurity, SWT.NONE);
		lblPass.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPass.setText("Pass 1:");
		
		text_pass1 = new Text(grpSecurity, SWT.BORDER);
		text_pass1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ps.setProperty( ContextProperties.PASS_1, (( Text )e.item ).getText() );
			}
		});
		text_pass1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblPass_1 = new Label(grpSecurity, SWT.NONE);
		lblPass_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPass_1.setText("Pass 2:");
		
		text_pass2 = new Text(grpSecurity, SWT.BORDER);
		text_pass2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ps.setProperty( ContextProperties.PASS_1, (( Text )e.item ).getText() );
			}
		});
		text_pass2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

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
	
	
	public JxseContextPropertySource getPropertySource() {
		return ps;
	}

	public void init(){
		ps = new JxseContextPropertySource( this.plugin_id, this.identifier);
		Object obj = ps.getDefault( ContextProperties.HOME_FOLDER ).toString();
		if( obj != null )
			this.text_home_folder.setText( obj.toString() );
		obj = ps.getDefault( ContextProperties.PLUGIN_ID );
		if( obj != null ){
			this.lbl_plugin_id.setText( " " + obj.toString() );
			this.text_id.setText( obj.toString() + S_JXSE_CONTEXT_1 );
		}
		obj = ps.getDefault( ContextProperties.IDENTIFIER );
		if( obj != null )
			this.text_identifier.setText( obj.toString() );
		obj = ps.getDefault( ContextProperties.PEER_ID );
		this.combo.setItems( AbstractJxsePreferences.getConfigModes());
		this.combo.select(ConfigMode.EDGE.ordinal());
		this.spinner.setMinimum( JxseContextPropertySource.DEF_MIN_PORT );
		this.spinner.setMaximum( JxseContextPropertySource.DEF_MAX_PORT );
		this.spinner.setSelection( (int) ps.getDefault( ContextProperties.PORT ));

		this.btnAutoStart.setSelection((boolean) ps.getDefaultDirectives( ContextDirectives.AUTO_START ));
		this.btnPersist.setSelection((boolean) ps.getDefaultDirectives( ContextDirectives.PEER_ID_PERSIST ));
		boolean create = (boolean) ps.getDefaultDirectives( ContextDirectives.PEER_ID_CREATE );
		this.btnGenerate.setSelection(create);
		this.combo_peer_id.setEnabled(!create);
		
		this.combo_template.setItems( TemplateOptions.getValues() );
		this.combo_template.select(1);
	}

	public void setPlugin_id(String plugin_id) {
		this.plugin_id = plugin_id;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
	public TemplateOptions getSelectedOption() {
		return TemplateOptions.values()[ this.combo_template.getSelectionIndex() ];
	}

	/**
	 * Complete the view by filling in the properties and directives
	 */
	public boolean complete() throws Exception{
		if( Utils.isNull( this.text_id.getText()))
			return false;
		ps.setId( this.text_id.getText() );
		if( !ps.setProperty( ContextProperties.HOME_FOLDER, URI.create( this.text_home_folder.getText() )))
			return false;
		if( !ps.setProperty( ContextProperties.PLUGIN_ID, this.lbl_plugin_id.getText() ))
			return false;
		if( !ps.setProperty( ContextProperties.IDENTIFIER, this.text_identifier.getText() ))
			return false;
		if( !ps.setProperty( ContextProperties.CONFIG_MODE, this.combo.getText() ))
			return false;
		if( !ps.setProperty( ContextProperties.PORT, this.spinner.getText() ))
			return false;
		if( !ps.setProperty( ContextProperties.PASS_1, this.text_pass1.getText() ))
			return false;
		if( !ps.setProperty( ContextProperties.PASS_2, this.text_pass2.getText() ))
			return false;
		if( !ps.setDirective( ContextDirectives.AUTO_START, this.btnAutoStart.getSelection() ))
			return false;
		if( !ps.setDirective( ContextDirectives.PEER_ID_CREATE, this.btnGenerate.getSelection() ))
			return false;
		return ps.setDirective( ContextDirectives.PEER_ID_PERSIST, this.btnPersist.getSelection() );
	}
}