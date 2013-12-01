package org.eclipselabs.jxse.ui.property;

import net.osgi.jxse.component.IJxseComponent;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

public class JxseComponentAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		  if((adapterType != IPropertySource.class ) && ( adapterType != IJxseComponent.class ))
			  return null;
		  if(!( adaptableObject instanceof IJxseComponent ))
			  return null;
		  IJxseComponent<?,?> component = (IJxseComponent<?,?>) adaptableObject;
		  JxseAdapterFactory factory = new JxseAdapterFactory();
		  Object retval = factory.getAdapter(component, adapterType);
		  if( retval != null )
			  return retval;
		  return new JxseComponentPropertySource( component, null );	
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {
		return new Class[]{IPropertySource.class };
	}

}
