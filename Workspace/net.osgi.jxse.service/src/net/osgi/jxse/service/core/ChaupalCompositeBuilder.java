package net.osgi.jxse.service.core;

import java.util.Iterator;

import net.osgi.jxse.advertisement.AdvertisementPropertySource;
import net.osgi.jxse.advertisement.JxseAdvertisementFactory;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.builder.CompositeBuilder;
import net.osgi.jxse.component.ModuleNode;
import net.osgi.jxse.context.ContextModule;
import net.osgi.jxse.discovery.DiscoveryPropertySource;
import net.osgi.jxse.discovery.DiscoveryServiceFactory;
import net.osgi.jxse.factory.FactoryNode;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.NetworkManagerFactory;
import net.osgi.jxse.pipe.PipeServiceFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.properties.IJxseDirectives.Contexts;
import net.osgi.jxse.service.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jxse.service.discovery.ChaupalDiscoveryServiceFactory;
import net.osgi.jxse.service.network.ChaupalNetworkManagerFactory;
import net.osgi.jxse.service.pipe.ChaupalPipeFactory;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class ChaupalCompositeBuilder extends CompositeBuilder {

	public ChaupalCompositeBuilder( ModuleNode<ContextModule> root ) {
		super( root );
	}
	
	/**
	 * This builder overrides the default behaviour by allowing a jxse service instead of a default JXTA service .
	 * The factory has just been created, so components have not been created and nothing has been started yet.
	 */
	@Override
	protected FactoryNode<?> createNode(ComponentNode<?> node, IComponentFactory<?> factory) {
		if( node == null )
			return super.createNode(node, factory);
		
		IComponentFactory<?> jxseFactory = getFactoryFromType( (IComponentFactory<?>) node.getData(), (IComponentFactory<?>) factory );
		this.extendPropertySource( factory.getPropertySource() );
		return super.createNode(node, jxseFactory);
	}

	/**
	 * extends the property source with additional property sources, if needed
	 * @param source
	 */
	protected void extendPropertySource( IJxsePropertySource<?> source ){
		if( source.getComponentName().equals( Components.PIPE_SERVICE.toString() ))
			this.extendPipeServicePropertySource(source);
		if( source.getComponentName().equals( Components.ADVERTISEMENT_SERVICE.toString() ))
			this.extendAdvertisementServicePropertySource(source);
	}

	/**
	 * Extend the pipe service
	 * @param source
	 */
	protected void extendPipeServicePropertySource( IJxsePropertySource<?> source ){
		IJxsePropertySource<?> child = source.getChild( Components.ADVERTISEMENT_SERVICE.toString());
		if( child  != null )
			return;
		AdvertisementPropertySource adsource = new AdvertisementPropertySource( source);
		Iterator<IJxseDirectives> iterator =  source.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext() ){
			directive = iterator.next();
			adsource.setDirective(directive, source.getDirective( directive ));
		}
		adsource.setDirective( AdvertisementDirectives.TYPE, AdvertisementTypes.PIPE.toString() );
		source.addChild( adsource );
	}

	/**
	 * Extend the discovery service
	 * @param source
	 */
	protected void extendAdvertisementServicePropertySource( IJxsePropertySource<?> source ){
		IJxsePropertySource<?> child = source.getChild( Components.DISCOVERY_SERVICE.toString());
		if( child  != null )
			return;
		DiscoveryPropertySource discsource = new DiscoveryPropertySource( source);
		Iterator<IJxseDirectives> iterator =  source.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext() ){
			directive = iterator.next();
			discsource.setDirective(directive, source.getDirective( directive ));
		}
		source.addChild( discsource );
	}


	/**
	 * Get the correct factory by checking the type
	 * @param factory
	 * @return
	 */
	protected static IComponentFactory<?> getFactoryFromType(  IComponentFactory<?> parent, IComponentFactory<?> factory ){
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
			if( factory instanceof PipeServiceFactory )
				return new ChaupalPipeFactory((PipeServiceFactory) factory );
		case JXSE:
		default:
			return factory;
		}
	}

}
