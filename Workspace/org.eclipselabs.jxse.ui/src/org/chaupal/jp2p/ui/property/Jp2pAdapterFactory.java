package org.chaupal.jp2p.ui.property;

import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jp2p.component.IJp2pComponent;

import org.chaupal.jp2p.ui.property.advertisement.AdvertisementPropertySource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

public class Jp2pAdapterFactory implements IAdapterFactory {

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		  if(adapterType != IPropertySource.class )
			  return null;
		  if( adaptableObject instanceof IJp2pComponent )
			  return this.getPropertySource(((IJp2pComponent) adaptableObject) );
		  return null;
	}

	/**
	 * Get the correct property source
	 * @param adaptableObject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected IPropertySource getPropertySource( IJp2pComponent<?> adaptableObject ){
		  if( adaptableObject.getModule() instanceof NetworkManager )
			  return new NetworkManagerPropertySource( (IJp2pComponent<NetworkManager>) adaptableObject );
		  if( adaptableObject.getModule() instanceof NetworkConfigurator )
			  return new NetworkConfiguratorPropertySource((IJp2pComponent<NetworkConfigurator>) adaptableObject);
		  if( adaptableObject.getModule() instanceof Advertisement )
			  return new AdvertisementPropertySource( (IJp2pComponent<Advertisement>)adaptableObject);
		  if( adaptableObject.getModule() instanceof PeerGroup )
			  return new PeerGroupPropertySource( (IJp2pComponent<PeerGroup> )adaptableObject);
		  return new Jp2pComponentPropertySource( adaptableObject, Jp2pComponentPropertySource.S_PROPERTY_JP2P_TEXT );			
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[]{IPropertySource.class };
	}

}
