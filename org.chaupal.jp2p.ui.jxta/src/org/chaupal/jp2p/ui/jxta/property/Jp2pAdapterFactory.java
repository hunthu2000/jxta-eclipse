package org.chaupal.jp2p.ui.jxta.property;

import net.jp2p.container.IJp2pContainer;
import net.jxta.configuration.JxtaConfiguration;
import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

public class Jp2pAdapterFactory implements IAdapterFactory {

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		  if(adapterType != IPropertySource.class )
			  return null;
		  if( adaptableObject instanceof IJp2pContainer )
			  return this.getPropertySource(( IJp2pContainer )adaptableObject );
		  return this.getPropertySource(adaptableObject);
	}

	/**
	 * Get the correct property source
	 * @param adaptableObject
	 * @return
	 */
	protected IPropertySource getPropertySource( Object adaptableObject ){
		  //if( adaptableObject instanceof NetworkManager )
		//	  return new NetworkManagerPropertySource(( NetworkManager )adaptableObject );
		 // if( adaptableObject instanceof NetworkConfigurator )
		//	  return new NetworkConfiguratorPropertySource(( NetworkConfigurator )adaptableObject);
		 // if( adaptableObject instanceof Advertisement )
		//	  return new AdvertisementPropertySource(( Advertisement )adaptableObject);
		 // if( adaptableObject instanceof PeerGroup )
	//		  return new PeerGroupPropertySource(( PeerGroup )adaptableObject);
//		  if( adaptableObject instanceof JxtaConfiguration )
//			  return new JxtaConfigurationPropertySource(( JxtaConfiguration )adaptableObject);
		  return null;			
	}

	/**
	 * Get the correct property source
	 * @param adaptableObject
	 * @return
	 */
	protected IPropertySource getPropertySource( IJp2pContainer<?> component ){
		Object module = component.getModule();  
		return this.getPropertySource( module );			
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[]{IPropertySource.class };
	}

}
