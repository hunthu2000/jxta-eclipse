package net.osgi.jxse.service.core;

import net.jxta.document.AdvertisementFactory;
import net.osgi.jxse.advertisement.JxseAdvertisementFactory;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.CompositeBuilder;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseDirectives.Types;
import net.osgi.jxse.service.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jxse.service.discovery.ChaupalDiscoveryServiceFactory;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class JxseCompositeBuilder<T extends Object, U extends Enum<U>, V extends IJxseDirectives> extends CompositeBuilder<T,U,V> {

	public JxseCompositeBuilder(IJxsePropertySource<U,V> propertySource) {
		super(propertySource);
	}

	
	/**
	 * This builder overrides the default behaviour by allowing a jxse service instead of a default JXTA service 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected ComponentNode<?, ?, ?> getNode(ComponentNode<?, ?, ?> node, IComponentFactory<?, ?, ?> factory) {
		IComponentFactory<?, ?, ?> jxseFactory = getFactoryFromType( (IComponentFactory<?, ?, IJxseDirectives>) factory );
		return super.getNode(node, jxseFactory);
	}

	/**
	 * Get the correct factory by checking the type
	 * @param factory
	 * @return
	 */
	protected static IComponentFactory<?,?,IJxseDirectives> getFactoryFromType( IComponentFactory<?,?,IJxseDirectives> factory ){
		String typeStr = (String) factory.getPropertySource().getDirective( Directives.CONTEXT );
		if( Utils.isNull(typeStr))
			return factory;
		Types type = Types.valueOf( StringStyler.styleToEnum( typeStr ));
		switch( type ){
		case CHAUPAL:
			if( factory instanceof DiscoveryServiceFactory )
				return new ChaupalDiscoveryServiceFactory((DiscoveryServiceFactory) factory );
			if( factory instanceof AdvertisementFactory )
				return new ChaupalAdvertisementFactory((JxseAdvertisementFactory) factory );
		case JXSE:
		default:
			return factory;
		}
	}

}
