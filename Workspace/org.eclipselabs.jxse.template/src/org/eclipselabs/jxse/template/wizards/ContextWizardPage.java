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
package org.eclipselabs.jxse.template.wizards;

import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.preferences.JxsePreferences;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
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

public class ContextWizardPage extends WizardPage {
	
	private static final String S_MSG_JXSE_CONTEXT_PROPS = "JXSE Context Properties";
	private static final String S_MSG_SET_JXSE_CONTEXT_PROPS = "Set JXSE Context Properties";
	
	private Text text_identifier;
	private Text text_plugin_id;
	private Text text_home_folder;
	private Combo combo;
	
	private Button btnAutoStart;
	private Combo combo_peer_id;
	private Button btnGenerate;
	private Button btnPersist;

	/**
	 * @wbp.parser.constructor
	 */
	public ContextWizardPage(String pageName) {
		super(pageName);
		setTitle( S_MSG_JXSE_CONTEXT_PROPS);
		setDescription(S_MSG_SET_JXSE_CONTEXT_PROPS);
	}

	public ContextWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Identifier:");
		
		text_identifier = new Text(container, SWT.BORDER);
		text_identifier.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Plugin-Id:");
		
		text_plugin_id = new Text(container, SWT.BORDER);
		text_plugin_id.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
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
		this.init();
	}
	
	protected void init(){
		JxseContextPropertySource ps = new JxseContextPropertySource( this.text_plugin_id.getText(), this.text_identifier.getText());
		Object obj = ps.getDefault( ContextProperties.HOME_FOLDER ).toString();
		if( obj != null )
			this.text_home_folder.setText( obj.toString() );
		obj = ps.getDefault( ContextProperties.IDENTIFIER );
		if( obj != null )
			this.text_identifier.setText( obj.toString() );
		obj = ps.getDefault( ContextProperties.PEER_ID );
		this.combo.setItems( JxsePreferences.getConfigModes());
		this.combo.select(ConfigMode.EDGE.ordinal());
		
		this.btnAutoStart.setSelection((boolean) ps.getDefaultDirectives( ContextDirectives.AUTO_START ));
		this.btnPersist.setSelection((boolean) ps.getDefaultDirectives( ContextDirectives.PEER_ID_PERSIST ));
		boolean create = (boolean) ps.getDefaultDirectives( ContextDirectives.PEER_ID_CREATE );
		this.btnGenerate.setSelection(create);
		this.combo_peer_id.setEnabled(!create);
	}
}