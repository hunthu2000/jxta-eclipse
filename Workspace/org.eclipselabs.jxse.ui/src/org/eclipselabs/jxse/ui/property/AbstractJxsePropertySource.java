package org.eclipselabs.jxse.ui.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponent.ModuleProperties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipselabs.jxse.ui.property.descriptors.AbstractControlPropertyDescriptor;
import org.eclipselabs.jxse.ui.property.descriptors.TextBoxPropertyDescriptor;

public abstract class AbstractJxsePropertySource<T extends Object> implements IPropertySource {

	public static final String S_PROPERTY_JXTA_TEXT = "JXTA";

	private T module;
	private Properties defaults ;

	protected AbstractJxsePropertySource( T module ) {
		this( module, new Properties());
	}

	protected AbstractJxsePropertySource( IJxseComponent<T> component ) {
		this( component.getModule(), new Properties());
		Iterator<?> iterator = component.iterator();
		Object key, value;
		while( iterator.hasNext()){
			key = iterator.next();
			value = component.getProperty(key);
			if( value != null )
			    this.defaults.put(key, value);
		}
	}

	protected AbstractJxsePropertySource( T module, Properties defaults ) {
		this.module = module;
		this.defaults = defaults;
	}

	protected T getModule() {
		return module;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	/**
	 * Returns true if the given property can be modified
	 * @param id
	 * @return
	 */
	public abstract boolean isEditable( Object id );
	
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Collection<ModuleProperties> properties = new ArrayList<ModuleProperties>();
		for( ModuleProperties property: ModuleProperties.values()){
			if( defaults.get( property ) != null )
				properties.add(property);
		}
		return getPropertyDescriptors( properties.toArray( new ModuleProperties[ properties.size()]));
	}

	@Override
	public Object getPropertyValue(Object id) {
		if(!( id instanceof ModuleProperties ))
			return null;
		ModuleProperties property = ( ModuleProperties )id;
		return defaults.get( property );
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if(!( id instanceof ModuleProperties ))
			return;
		ModuleProperties property = ( ModuleProperties )id;
		defaults.put( property, value);
	}

	@Override
	public boolean isPropertySet(Object id) {
		Object defaultValue = this.defaults.get( id );
		if( defaultValue == null )
			return false;
		return ( !this.getPropertyValue(id).equals( defaultValue ));
	}

	@Override
	public void resetPropertyValue(Object id) {
		Object defaultValue = this.defaults.get( id );
		if( defaultValue == null )
			return;
		this.setPropertyValue(id, defaultValue );
	}

	/**
	 * Parses a property and puts the results in a [id,value,category] table,
	 * which van be used to fill a property descriptor
	 * @param property
	 * @return
	 */
	protected static String[] parseProperty( Object property ) {
		if( property == null )
			return null;
		String[] retval = new String[3];
		retval[0] = property.toString();
		retval[1] = property.toString();
		retval[2] = S_PROPERTY_JXTA_TEXT;
		String[] split = property.toString().split("[.]");
		if( split.length > 1 ){
			retval[2] = split[0];
			retval[1] = retval[1].replace(retval[2] + ".", "");
		}
		return retval;
	}

	/**
	 * Get the (text) property descriptors from the given values
	 * @param properties
	 * @return
	 */
	protected static AbstractControlPropertyDescriptor<?>[] getPropertyDescriptors( Object[] properties ) {
		if( properties == null )
			return null;
		Collection<AbstractControlPropertyDescriptor<?>> descriptors = 
				new ArrayList<AbstractControlPropertyDescriptor<?>>();
		for( int i=0; i<properties.length; i++ ){
			String[] parsed = parseProperty(properties[i]);
			TextBoxPropertyDescriptor textDescriptor = new TextBoxPropertyDescriptor( properties[i], parsed[1]);
			textDescriptor.setCategory(parsed[2]);
			descriptors.add( textDescriptor );
		}
		return descriptors.toArray( new AbstractControlPropertyDescriptor[descriptors.size()]);
	}

}