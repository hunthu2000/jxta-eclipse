package org.eclipselabs.jxse.ui.network;

import net.osgi.jxse.network.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;

import org.eclipse.swt.widgets.Composite;
import org.eclipselabs.jxse.ui.property.databinding.BooleanDataBinding;
import org.eclipselabs.jxse.ui.property.databinding.SpinnerDataBinding;

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
		//if( value != null)
			new BooleanDataBinding<NetworkConfiguratorProperties>( NetworkConfiguratorProperties.TCP_8ENABLED, source, this.btnEnabled);
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
		new SpinnerDataBinding<NetworkConfiguratorProperties>( NetworkConfiguratorProperties.TCP_8PORT ,source, 
				this.portSpinner, NetworkConfiguratorProperties.TCP_8START_PORT, NetworkConfiguratorProperties.TCP_8END_PORT );
	}
}
