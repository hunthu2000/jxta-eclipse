package net.osgi.jp2p.builder;

import net.osgi.jp2p.factory.ComponentBuilderEvent;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;

public interface IContainerBuilder {

	public abstract boolean addFactory(IComponentFactory<?> factory);

	public abstract boolean removeFactory(IComponentFactory<Object> factory);

	public abstract IComponentFactory<?> getFactory(String name);

	public IComponentFactory<Object>[] getFactories();
	
	/**
	 * Returns the factory who'se source matched the given one
	 * @param source
	 * @return
	 */
	public abstract IComponentFactory<?> getFactory(
			IJp2pPropertySource<?> source);

	/**
	 * Returns true if all the factorys have been completed
	 * @return
	 */
	public abstract boolean isCompleted();

	/**
	 * List the factorys that did not complete
	 * @return
	 */
	public abstract String listModulesNotCompleted();

	/**
	 * Perform a request for updating the container
	 * @param event
	 */
	public abstract void updateRequest(ComponentBuilderEvent<?> event);

	/**
	 * Get the default factory for this container
	 * @param parent
	 * @param componentName
	 * @return
	 */
	public abstract IComponentFactory<?> getDefaultFactory(
			IJp2pPropertySource<IJp2pProperties> parentSource,
			String componentName);

	/** Add a factory with the given component name to the container. use the given component name and parent,
	 * if 'createsource' is true, the property source is immediately created, and 'blockcreation' means that
	 * the builder will not create the factory. instead the parent factory should provide for this 
	 * 
	 * @param componentName
	 * @param createSource
	 * @param blockCreation
	 * @return
	 */
	public abstract IComponentFactory<?> addFactoryToContainer(
			String componentName,
			IJp2pPropertySource<IJp2pProperties> parentSource,
			boolean createSource, boolean blockCreation);

	/**
	 * Get or create a corresponding factory for a child component of the given source, with the given component name.
	 * @param source: the source who should have a child source
	 * @param componentName: the required component name of the child
	 * @param createSource: create the property source immediately
	 * @param blockCreation: do not allow the builder to create the component
	 * @return
	 */
	public abstract IComponentFactory<?> getOrCreateChildFactory(
			IJp2pPropertySource<IJp2pProperties> source, String componentName,
			boolean createSource, boolean blockCreation);

}