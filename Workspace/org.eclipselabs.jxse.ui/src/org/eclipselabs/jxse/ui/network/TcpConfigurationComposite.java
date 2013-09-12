package org.eclipselabs.jxse.ui.network;

import net.osgi.jxse.network.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;

import org.eclipse.swt.widgets.Composite;

public class TcpConfigurationComposite extends AbstractProtocolConfigurationComposite {
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TcpConfigurationComposite(Composite parent, int style) {
		super(parent, style);
	}

	public void init( NetworkConfigurationPropertySource source ){
		super.init( source );
		this.btnEnabled.setSelection((boolean) source.getDefault( NetworkConfiguratorProperties.HTTP_8ENABLED ));
		this.btnIncomingStatus.setSelection((boolean) source.getDefault( NetworkConfiguratorProperties.HTTP_8INCOMING_STATUS ));
		this.btnOutgoingStatus.setSelection((boolean) source.getDefault( NetworkConfiguratorProperties.HTTP_8OUTGOING_STATUS ));
		this.btnPublicAddress.setSelection((boolean) source.getDefault( NetworkConfiguratorProperties.HTTP_8PUBLIC_ADDRESS ));
		this.spinner.setSelection((int) source.getDefault( NetworkConfiguratorProperties.HTTP_8PUBLIC_ADDRESS ));
		this.text.setText((String) source.getDefault( NetworkConfiguratorProperties.HTTP_8INTERFACE_ADDRESS));
		this.text.setText((String) source.getDefault( NetworkConfiguratorProperties.HTTP_8INTERFACE_ADDRESS));
	}
}
