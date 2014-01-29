package org.chaupal.jp2p.ui.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.jp2p.container.component.IJp2pComponent;
import net.jp2p.container.properties.IJp2pProperties;
import net.osgi.jp2p.chaupal.utils.Utils;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class Jp2pComponentUIPropertySource implements IPropertySource {

	public static final String S_PROPERTY_JXTA_COMPONENT_ID = "org.condast.jxta.service.component";
	public static final String S_PROPERTY_JXTA_COMPONENT_TEXT_ID = "org.condast.jxta.service.component.text";

	public static final String S_PROPERTY_JP2P_TEXT = "JP2P";
	public static final String S_PROPERTY_JTTA_SERVICE_COMPONENT_TEXT = "ServiceComponent";
	public static final String S_PROPERTY_TEXT = "Properties";

	private String defaultText;
	private IJp2pComponent<?> component;

	public Jp2pComponentUIPropertySource( IJp2pComponent<?> component, String defaultText ) {
		super();
		this.defaultText = defaultText;
		this.component = component;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor descriptor = new Jp2pComponentPropertyDescriptor( S_PROPERTY_JXTA_COMPONENT_ID, S_PROPERTY_JTTA_SERVICE_COMPONENT_TEXT );
		descriptor.setCategory(S_PROPERTY_JP2P_TEXT);
		Iterator<IJp2pProperties> iterator = this.component.getPropertySource().propertyIterator();
		Collection<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		descriptors.add( descriptor);
		IJp2pProperties key;
		String attribute;
		TextPropertyDescriptor textDescriptor; 
		while( iterator.hasNext() ){
			key = iterator.next();
			String category = component.getPropertySource().getCategory( key );
			attribute = key.toString();
			String[] split = attribute.split("[.]");
			if( split.length > 1 ){
				attribute = split[split.length - 1];
				if( net.jp2p.container.utils.Utils.isNull( category) )
					category = key.toString().replace( "." + attribute, "");
			}
			textDescriptor = new TextPropertyDescriptor( key, attribute);
			if( net.jp2p.container.utils.Utils.isNull( category ))
				category = S_PROPERTY_JP2P_TEXT;
			textDescriptor.setCategory( category);
			descriptors.add( textDescriptor);
		}
		return descriptors.toArray( new IPropertyDescriptor[ descriptors.size()]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals( S_PROPERTY_JXTA_COMPONENT_ID))
			return Utils.getLabel( this.component );
		return this.component.getPropertySource().getProperty( (IJp2pProperties) id );
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