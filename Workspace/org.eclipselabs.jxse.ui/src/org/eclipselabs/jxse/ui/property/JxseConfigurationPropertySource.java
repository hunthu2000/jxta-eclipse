package org.eclipselabs.jxse.ui.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;

import net.jxta.configuration.JxtaConfiguration;
import net.osgi.jxse.utils.StringStyler;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class JxseConfigurationPropertySource extends AbstractJxsePropertySource<JxtaConfiguration> {

	public JxseConfigurationPropertySource( JxtaConfiguration configuration ) {
		super( configuration, configuration );
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Collection<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		JxtaConfiguration configuration = super.getModule();
		Enumeration<Object> keys = configuration.keys();
		Object key, value;
		while( keys.hasMoreElements() ){
			key = keys.nextElement();
			value = configuration.get(key);
			if( value != null )
				descriptors.add( new TextPropertyDescriptor( key, StringStyler.prettyString( key.toString() )));
		}
		if( super.getPropertyDescriptors() != null )
			descriptors.addAll( Arrays.asList( super.getPropertyDescriptors()));
		return descriptors.toArray( new IPropertyDescriptor[ descriptors.size()]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		JxtaConfiguration configuration = super.getModule();
		Object value = configuration.get(id);
		if( value != null )
			return value;
		return super.getPropertyValue(id);
	}

	/**
	 * Returns true if the given property can be modified
	 * @param id
	 * @return
	 */
	@Override
	public boolean isEditable( Object id ){
		return false;
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		JxtaConfiguration configuration = super.getModule();
		Object key = configuration.get(id);
		if( key == null )
			configuration.put( id, value);
		else
		  super.setPropertyValue(id, value);
	}
}