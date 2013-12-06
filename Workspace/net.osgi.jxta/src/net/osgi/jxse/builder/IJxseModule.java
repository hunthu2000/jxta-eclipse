package net.osgi.jxse.builder;

import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;

public interface IJxseModule<T extends Object, U extends Object, V extends IJxseDirectives> {

	/**
	 * The name of the module
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the property source that is used for the factor
	 * @return
	 */
	public IJxsePropertySource<U,V> getPropertySource();
	
	/**
	 * Convenience method to set a property of the property source
	 * @param id
	 * @param value
	 */
	public void setProperty( U id, Object value );
	
	/**
	 * Create the factory that will make the component
	 * @return
	 */
	public IComponentFactory<T,U,V> createFactory();
}
