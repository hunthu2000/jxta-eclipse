package net.osgi.jxse.builder;

import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public interface IJxseModule<T extends Object> {

	/**
	 * The name of the module
	 * @return
	 */
	public String getComponentName();
	
	/**
	 * Get the property source that is used for the factor
	 * @return
	 */
	public IJxsePropertySource<IJxseProperties> createPropertySource( IJxsePropertySource<?> parent );

	/**
	 * Get the property source that is used for the factor
	 * @return
	 */
	public IJxsePropertySource<IJxseProperties> getPropertySource();

	/**
	 * Convenience method to set a property of the property source
	 * @param id
	 * @param value
	 */
	public void setProperty( IJxseProperties id, Object value );
	
	/**
	 * Create the factory that will make the component
	 * @param provider
	 * @return
	 */
	public IComponentFactory<T,IJxseProperties> createFactory( IPeerGroupProvider provider );
}
