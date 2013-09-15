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
		Object value = source.getDefault( NetworkConfiguratorProperties.TCP_8ENABLED  );
		if( value != null)
			this.btnEnabled.setSelection((boolean)value );
		value = source.getDefault( NetworkConfiguratorProperties.TCP_8INCOMING_STATUS  );
		if( value != null)
			this.btnIncomingStatus.setSelection((boolean) value);
		value = source.getDefault( NetworkConfiguratorProperties.TCP_8OUTGOING_STATUS  );
		if( value != null)
			this.btnOutgoingStatus.setSelection((boolean)value );
		value = source.getDefault( NetworkConfiguratorProperties.TCP_8PUBLIC_ADDRESS  );
		if( value != null)
			this.btnPublicAddress.setSelection((boolean) value );
		value = source.getDefault( NetworkConfiguratorProperties.TCP_8PUBLIC_ADDRESS_EXCLUSIVE  );
		if( value != null)
			this.btnExclusive.setSelection((boolean) value );
		value = source.getDefault( NetworkConfiguratorProperties.TCP_8INTERFACE_ADDRESS  );
		if( value != null)
			this.interfaceAddressText.setText((String)value );
		this.portSpinner.setMaximum(65536);
		value = source.getDefault( NetworkConfiguratorProperties.TCP_8PORT  );
		if( value != null){
			int port = ( int )value;

			this.portSpinner.setSelection( port );
		}
		value = source.getDefault( NetworkConfiguratorProperties.TCP_8START_PORT  );
		if( value != null)
			this.portSpinner.setMinimum(( int )value );
		value = source.getDefault( NetworkConfiguratorProperties.TCP_8END_PORT  );
		if( value != null)
			this.portSpinner.setMaximum(( int )value );
	}
}
