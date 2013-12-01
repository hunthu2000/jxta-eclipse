package org.eclipselabs.jxse.ui.property;

import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.component.IJxseComponent;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipselabs.jxse.ui.property.advertisement.AdvertisementPropertySource;

public class JxseAdapterFactory implements IAdapterFactory {

	@SuppressWarnings({ "rawtypes" })
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		  if(adapterType != IPropertySource.class )
			  return null;
		  if( adaptableObject instanceof IJxseComponent )
			  return this.getPropertySource(((IJxseComponent) adaptableObject).getModule() );
		  return this.getPropertySource(adaptableObject);
	}

	/**
	 * Get the correct property source
	 * @param adaptableObject
	 * @return
	 */
	protected IPropertySource getPropertySource( Object adaptableObject ){
		  if( adaptableObject instanceof NetworkManager )
			  return new NetworkManagerPropertySource(( NetworkManager )adaptableObject );
		  if( adaptableObject instanceof NetworkConfigurator )
			  return new NetworkConfiguratorPropertySource(( NetworkConfigurator )adaptableObject);
		  if( adaptableObject instanceof Advertisement )
			  return new AdvertisementPropertySource(( Advertisement )adaptableObject);
		  if( adaptableObject instanceof PeerGroup )
			  return new PeerGroupPropertySource(( PeerGroup )adaptableObject);
		  return null;			
	}

	/**
	 * Get the correct property source
	 * @param adaptableObject
	 * @return
	 */
	protected IPropertySource getPropertySource( IJxseComponent<?,?> component ){
		Object module = component.getModule();  
		return this.getPropertySource( module );			
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[]{IPropertySource.class };
	}

}
