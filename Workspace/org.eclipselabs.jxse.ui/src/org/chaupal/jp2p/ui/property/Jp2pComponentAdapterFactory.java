package org.chaupal.jp2p.ui.property;

import net.osgi.jp2p.component.IJp2pComponent;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

public class Jp2pComponentAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		  if((adapterType != IPropertySource.class ) && ( adapterType != IJp2pComponent.class ))
			  return null;
		  if(!( adaptableObject instanceof IJp2pComponent ))
			  return null;
		  IJp2pComponent<?> component = (IJp2pComponent<?>) adaptableObject;
		  Jp2pAdapterFactory factory = new Jp2pAdapterFactory();
		  Object retval = factory.getAdapter(component, adapterType);
		  if( retval != null )
			  return retval;
		  return new Jp2pComponentUIPropertySource( component, null );	
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[]{IPropertySource.class };
	}

}
