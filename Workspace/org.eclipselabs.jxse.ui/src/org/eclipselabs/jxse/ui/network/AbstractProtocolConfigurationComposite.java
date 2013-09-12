package org.eclipselabs.jxse.ui.network;

import net.osgi.jxse.network.NetworkConfigurationPropertySource;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractProtocolConfigurationComposite extends Composite {
	
	private NetworkConfigurationPropertySource source;
	
	protected Button btnEnabled;
	protected Text text;
	protected Spinner spinner;
	protected Button btnIncomingStatus;
	protected Button btnOutgoingStatus;
	protected Button btnPublicAddress;
	protected Button btnExclusive;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public AbstractProtocolConfigurationComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		btnEnabled = new Button(this, SWT.CHECK);
		btnEnabled.setText("Enabled:");
		new Label(this, SWT.NONE);
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setBounds(0, 0, 55, 15);
		lblNewLabel.setText("Port:");
		
		spinner = new Spinner(this, SWT.BORDER);
		spinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		btnIncomingStatus = new Button(this, SWT.CHECK);
		btnIncomingStatus.setText("Incoming Status");
		new Label(this, SWT.NONE);
		
		btnOutgoingStatus = new Button(this, SWT.CHECK);
		btnOutgoingStatus.setText("ougoing status");
		new Label(this, SWT.NONE);
		
		btnPublicAddress = new Button(this, SWT.CHECK);
		btnPublicAddress.setText("Public Address:");
		
		btnExclusive = new Button(this, SWT.CHECK);
		btnExclusive.setText("Exclusive");
		
		Label lblInterfaceAddress = new Label(this, SWT.NONE);
		lblInterfaceAddress.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblInterfaceAddress.setText("Interface Address:");
		
		text = new Text(this, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}

	protected NetworkConfigurationPropertySource getSource() {
		return source;
	}


	public void init( NetworkConfigurationPropertySource source ){
		this.source = source;
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
