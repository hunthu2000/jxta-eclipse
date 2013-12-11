package net.osgi.jxse.service.core;

import net.osgi.jxse.advertisement.JxseAdvertisementFactory;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.CompositeBuilder;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseDirectives.Contexts;
import net.osgi.jxse.service.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jxse.service.discovery.ChaupalDiscoveryServiceFactory;
import net.osgi.jxse.service.network.ChaupalNetworkManagerFactory;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class JxseCompositeBuilder<T extends Object, U extends Object, V extends IJxseDirectives> extends CompositeBuilder<T,U,V> {

	public JxseCompositeBuilder(IJxsePropertySource<U,V> propertySource) {
		super(propertySource);
	}

	
	/**
	 * This builder overrides the default behaviour by allowing a jxse service instead of a default JXTA service 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected ComponentNode<?, ?, ?> createNode(ComponentNode<?, ?, ?> node, IComponentFactory<?, ?, ?> factory) {
		if( node == null )
			return super.createNode(node, factory);
		IComponentFactory<?, ?, ?> jxseFactory = getFactoryFromType( (IComponentFactory<?, IJxseProperties, IJxseDirectives>) node.getFactory(), (IComponentFactory<?, ?, IJxseDirectives>) factory );
		return super.createNode(node, jxseFactory);
	}

	/**
	 * Get the correct factory by checking the type
	 * @param factory
	 * @return
	 */
	protected static IComponentFactory<?,?,IJxseDirectives> getFactoryFromType(  IComponentFactory<?, IJxseProperties, IJxseDirectives> parent, IComponentFactory<?, ?,IJxseDirectives> factory ){
		String contextStr = (String) factory.getPropertySource().getDirective( Directives.CONTEXT );
		if( Utils.isNull(contextStr))
			return factory;
		Contexts context = Contexts.valueOf( StringStyler.styleToEnum( contextStr ));
		switch( context ){
		case CHAUPAL:
			if( factory instanceof NetworkManagerFactory )
				return new ChaupalNetworkManagerFactory( parent, ( NetworkManagerFactory )factory );
			if( factory instanceof DiscoveryServiceFactory )
				return new ChaupalDiscoveryServiceFactory((DiscoveryServiceFactory) factory );
			if( factory instanceof JxseAdvertisementFactory )
				return new ChaupalAdvertisementFactory((JxseAdvertisementFactory) factory );
		case JXSE:
		default:
			return factory;
		}
	}

}
