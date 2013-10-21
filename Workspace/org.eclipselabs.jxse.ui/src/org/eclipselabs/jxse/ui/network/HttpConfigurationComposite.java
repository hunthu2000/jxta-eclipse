package org.eclipselabs.jxse.ui.network;

import net.osgi.jxse.network.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;

import org.eclipse.swt.widgets.Composite;

public class HttpConfigurationComposite extends AbstractProtocolConfigurationComposite {
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public HttpConfigurationComposite(Composite parent, int style) {
		super(parent, style);
	}

	public void init( NetworkConfigurationPropertySource source ){
		super.init( source );
		Object value = source.getDefault( NetworkConfiguratorProperties.HTTP_8ENABLED  );
		if( value != null)
			this.btnEnabled.setSelection((boolean)value );
		value = source.getDefault( NetworkConfiguratorProperties.HTTP_8INCOMING_STATUS  );
		if( value != null)
			this.btnIncomingStatus.setSelection((boolean) value);
		value = source.getDefault( NetworkConfiguratorProperties.HTTP_8OUTGOING_STATUS  );
		if( value != null)
			this.btnOutgoingStatus.setSelection((boolean)value );
		value = source.getDefault( NetworkConfiguratorProperties.HTTP_8PUBLIC_ADDRESS  );
		if( value != null)
			this.btnPublicAddress.setSelection((boolean) value );
		value = source.getDefault( NetworkConfiguratorProperties.HTTP_8PUBLIC_ADDRESS_EXCLUSIVE  );
		if( value != null)
			this.btnExclusive.setSelection((boolean) value );
		value = source.getDefault( NetworkConfiguratorProperties.HTTP_8INTERFACE_ADDRESS  );
		if( value != null)
			this.interfaceAddressText.setText((String)value );
		value = source.getDefault( NetworkConfiguratorProperties.HTTP_8PORT  );
		if( value != null)
			this.portSpinner.setSelection(( int )value );
	}
}
