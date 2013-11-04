package org.eclipselabs.jxse.ui.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class CollectionPropertySource implements IPropertySource {

	public static final String S_PROPERTY_JXTA_COMPONENT_ID = "org.condast.jxta.service.component";
	public static final String S_PROPERTY_JXTA_COMPONENT_TEXT_ID = "org.condast.jxta.service.component.text";

	public static final String S_PROPERTY_JXTA_TEXT = "JXTA";
	public static final String S_PROPERTY_JTTA_SERVICE_COMPONENT_TEXT = "ServiceComponent";
	public static final String S_PROPERTY_TEXT = "Properties";

	private String defaultText;
	private List<Object> source;
	private String category;

	public CollectionPropertySource( String category, Collection<?> source, String defaultText ) {
		super();
		this.defaultText = defaultText;
		this.source = new ArrayList<Object>( source );
		this.category = category;
	}

	public CollectionPropertySource( String category, Object[] source, String defaultText ) {
		super();
		this.defaultText = defaultText;
		this.source = new ArrayList<Object>();
		this.category = category;
		for( Object item: source )
		  this.source.add( item ); 
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Collection<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		TextPropertyDescriptor textDescriptor; 
		int i=0;
		for( Object value: source ){
			textDescriptor = new TextPropertyDescriptor( i, value.toString() );
			textDescriptor.setCategory( category);
			descriptors.add( textDescriptor);
			i++;
		}
		return descriptors.toArray( new IPropertyDescriptor[ descriptors.size()]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		return this.source.get(( int )id );
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
