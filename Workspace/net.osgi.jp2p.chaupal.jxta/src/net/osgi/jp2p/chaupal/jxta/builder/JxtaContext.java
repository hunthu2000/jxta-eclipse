package net.osgi.jp2p.chaupal.jxta.builder;

import org.xml.sax.Attributes;

import net.osgi.jp2p.builder.IContainerBuilder;
import net.osgi.jp2p.builder.IJp2pContext;
import net.osgi.jp2p.builder.Jp2pContext;
import net.osgi.jp2p.chaupal.jxta.advertisement.ChaupalAdvertisementFactory;
import net.osgi.jp2p.chaupal.jxta.discovery.ChaupalDiscoveryServiceFactory;
import net.osgi.jp2p.chaupal.jxta.pipe.ChaupalPipeFactory;
import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.factory.JxtaFactoryUtils;
import net.osgi.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Contexts;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class JxtaContext implements IJp2pContext<Object> {

	public JxtaContext() {
	}

	@Override
	public String getName() {
		return Contexts.CHAUPAL.toString();
	}

	/**
	 * Returns true if the given component name is valid for this context
	 * @param componentName
	 * @return
	 */
	public boolean isValidComponentName( String contextName, String componentName ){
		if( !Utils.isNull( contextName ) && !Jp2pContext.isContextNameEqual(Contexts.JXTA.toString(), contextName ))
			return false;
		return JxtaComponents.isComponent( componentName );
	}

	/**
	 * Returns true if the given factory is a chaupal factory
	 * @param factory
	 * @return
	 */
	public boolean isChaupalFactory( IComponentFactory<?> factory ){
		if( factory instanceof ChaupalAdvertisementFactory )
			return true;
		if( factory instanceof ChaupalDiscoveryServiceFactory )
			return true;
		return ( factory instanceof ChaupalPipeFactory );
	}

	/**
	 * Change the factory to a Chaupal factory if required and available
	 * @param factory
	 * @return
	 */
	@Override
	public IComponentFactory<?> getFactory( IContainerBuilder builder, Attributes attributes, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		JxtaComponents component = JxtaComponents.valueOf( StringStyler.styleToEnum(componentName));
		String[] attrs;
		switch( component ){
		case ADVERTISEMENT:
			String adType = attributes.getValue( AdvertisementDirectives.TYPE.toString().toLowerCase() );
			attrs = new String[1];
			attrs[0] = adType;
			break;
		default:
			attrs = new String[0];
		}
		IComponentFactory<?> factory = JxtaFactoryUtils.getDefaultFactory(builder, attrs, parentSource, componentName);
		return factory;
	}

}
