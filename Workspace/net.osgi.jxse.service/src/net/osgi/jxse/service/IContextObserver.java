package net.osgi.jxse.service;

import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.factory.IComponentFactory;

public interface IContextObserver<T extends Object> {

	public void buildStarted( IJxseServiceContext<T> context );
	
	/**
	 * Observes the creation of a factory in a context 
	 * @param factory
	 */
	public void factoryCreated( IComponentFactory<?> factory );

	/**
	 * Observes the creation of a component in a context 
	 * @param factory
	 */
	public void componentCreated( Object component );

	public void buildCompleted( IJxseServiceContext<T> context );

}
