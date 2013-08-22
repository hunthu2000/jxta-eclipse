package org.eclipselabs.jxse.ui.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.service.utils.Utils;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class JxseComponentPropertySource implements IPropertySource {

	public static final String S_PROPERTY_JXTA_COMPONENT_ID = "org.condast.jxta.service.component";
	public static final String S_PROPERTY_JXTA_COMPONENT_TEXT_ID = "org.condast.jxta.service.component.text";

	public static final String S_PROPERTY_JXTA_TEXT = "JXTA";
	public static final String S_PROPERTY_JTTA_SERVICE_COMPONENT_TEXT = "ServiceComponent";
	public static final String S_PROPERTY_TEXT = "Properties";

	private String defaultText;
	private IJxseComponent<?> source;

	public JxseComponentPropertySource( IJxseComponent<?> source, String defaultText ) {
		super();
		this.defaultText = defaultText;
		this.source = source;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor descriptor = new JxseComponentPropertyDescriptor( S_PROPERTY_JXTA_COMPONENT_ID, S_PROPERTY_JTTA_SERVICE_COMPONENT_TEXT );
		descriptor.setCategory(S_PROPERTY_JXTA_TEXT);
		Iterator<?> iterator = this.source.iterator();
		Collection<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		descriptors.add( descriptor);
		Object key;
		String category = S_PROPERTY_JXTA_TEXT;
		String attribute;
		TextPropertyDescriptor textDescriptor; 
		while( iterator.hasNext() ){
			key = iterator.next();
			attribute = key.toString();
			String[] split = attribute.split("[.]");
			if( split.length > 1 ){
				category = split[0];
				attribute = attribute.replace(category + ".", "");
			}
			textDescriptor = new TextPropertyDescriptor( key, attribute);
			textDescriptor.setCategory( category);
			descriptors.add( textDescriptor);
		}
		return descriptors.toArray( new IPropertyDescriptor[ descriptors.size()]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals( S_PROPERTY_JXTA_COMPONENT_ID))
			return Utils.getLabel( this.source );
		return this.source.getProperty( id );
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (id.equals( S_PROPERTY_JXTA_COMPONENT_TEXT_ID )) {
			String curName = (String)getPropertyValue(id);
			return !curName.equals( this.defaultText );
		}
		return false;	
	}

	/**
	 * Currently not needed, there is no editing of properties, so a reset is not needed
	 * @param id
	 * @param value
	 */
	@Override
	public void resetPropertyValue(Object id) {
	}

	/**
	 * Currently not needed, there is no editing of properties
	 * @param id
	 * @param value
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
	}

}
