package net.osgi.jxse.builder;

import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public interface IJxseModule<T extends Object> extends ICompositeBuilderListener<Object>{

	/**
	 * The name of the module
	 * @return
	 */
	public String getComponentName();

	/**
	 * Get the parent of this module, or null if it ids a root
	 * @return
	 */
	public IJxseModule<?> getParent();
	
	/**
	 * Get the weight of the module. By definition, the context is 0, the startup service is 1,
	 * and networkmanager is 2.
	 * @return
	 */
	public int getWeight();
	
	/**
	 * Get the property source that is used for the factor
	 * @return
	 */
	public IJxsePropertySource<IJxseProperties> createPropertySource();

	/**
	 * Allows extension of the modules after the base functionality has been created
	 * @return
	 */
	public void extendModules();

	/**
	 * Get the property source that is used for the factor
	 * @return
	 */
	public IJxsePropertySource<IJxseProperties> getPropertySource();

	/**
	 * Returns true if the factory can be created
	 * @return
	 */
	public boolean canCreate();

	/**
	 * Create the factory that will make the component
	 * @param provider
	 * @return
	 */
	public IComponentFactory<T> createFactory();

	/**
	 * Get the factory;
	 * @param provider
	 * @return
	 */
	public IComponentFactory<T> getFactory();

	/**
	 * Returns true if the module is complete. this means that the factoryb was created succesfully
	 * @return
	 */
	public boolean isCompleted();

	/**
	 * Create the factory that will make the component
	 * @param provider
	 * @return
	 */
	public T getComponent();

	/**
	 * Returns true if the component can be activated
	 * @return
	 */
	public boolean canActivate();


}
