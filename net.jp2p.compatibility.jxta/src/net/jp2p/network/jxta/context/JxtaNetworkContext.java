package net.jp2p.network.jxta.context;

import org.xml.sax.Attributes;

import net.jp2p.chaupal.jxta.network.configurator.OverviewPreferences;
import net.jp2p.chaupal.jxta.network.http.Http2Preferences;
import net.jp2p.chaupal.jxta.network.http.HttpPreferences;
import net.jp2p.chaupal.jxta.network.multicast.MulticastPreferences;
import net.jp2p.chaupal.jxta.network.security.SecurityPreferences;
import net.jp2p.chaupal.jxta.network.tcp.TcpPreferences;
import net.jp2p.chaupal.jxta.root.network.NetworkManagerPreferences;
import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.partial.PartialPropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.IJp2pDirectives.Contexts;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.container.utils.Utils;
import net.jp2p.container.xml.IJp2pHandler;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaNetworkComponents;
import net.jp2p.jxta.seeds.SeedListPropertySource;
import net.jp2p.network.jxta.utils.JxtaFactoryUtils;

public class JxtaNetworkContext implements IJp2pContext {

	public JxtaNetworkContext() {
	}

	@Override
	public String getName() {
		return Contexts.JXTA.toString();
	}

	/**
	 * Get the supported services
	 */
	@Override
	public String[] getSupportedServices() {
		JxtaNetworkComponents[] components = JxtaNetworkComponents.values();
		String[] names = new String[ components.length ];
		for( int i=0; i<components.length; i++ )
			names[i] = components[i].toString();
		return names;
	}

	/**
	 * Returns true if the given component name is valid for this context
	 * @param componentName
	 * @return
	 */
	@Override
	public boolean isValidComponentName( String contextName, String componentName ){
		if( !Utils.isNull( contextName ) && !Jp2pContext.isContextNameEqual(Contexts.JXTA.toString(), contextName ))
			return false;
		return JxtaNetworkComponents.isComponent( componentName );
	}

	/**
	 * Change the factory to a Chaupal factory if required and available
	 * @param factory
	 * @return
	 */
	@Override
	public IPropertySourceFactory getFactory( IContainerBuilder builder, Attributes attributes, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		JxtaNetworkComponents component = JxtaNetworkComponents.valueOf( StringStyler.styleToEnum(componentName));
		String[] attrs;
		switch( component ){
		default:
			attrs = new String[0];
		}
		IPropertySourceFactory factory = JxtaFactoryUtils.getDefaultFactory(builder, attrs, parentSource, componentName);
		return factory;
	}

	@Override
	public Object createValue( String componentName, IJp2pProperties id ){
		return null;
	}
	
	/**
	 * Get the default factory for this container
	 * @param parent
	 * @param componentName
	 * @return
	 */
	@Override
	public IPropertyConvertor<String, Object> getConvertor( IJp2pPropertySource<IJp2pProperties> source ){
		String comp = StringStyler.styleToEnum( source.getComponentName());
		if( !JxtaNetworkComponents.isComponent( comp ))
			return null;
		JxtaNetworkComponents component = JxtaNetworkComponents.valueOf(comp);
		IPropertyConvertor<String, Object> convertor = null;
		switch( component ){
		case NETWORK_MANAGER:
			convertor = new NetworkManagerPreferences( (IJp2pWritePropertySource<IJp2pProperties>) source );
			break;			
		case NETWORK_CONFIGURATOR:
			convertor = new OverviewPreferences( (IJp2pWritePropertySource<IJp2pProperties>) source );
			break;
		case SEED_LIST:
			SeedListPropertySource slps = (SeedListPropertySource) source;
			//SeedInfo seedInfo = new SeedInfo((( IJp2pProperties )source.getKey()).name(), ( String )value );
			//slps.setProperty( (IJp2pProperties) property.getKey(), seedInfo );
			break;
		case TCP:
			convertor = new TcpPreferences( (PartialPropertySource) source );
			break;
		case HTTP:
			convertor = new HttpPreferences( (PartialPropertySource) source );
			break;
		case HTTP2:
			convertor = new Http2Preferences( (PartialPropertySource) source );
			break;
		case MULTICAST:
			convertor = new MulticastPreferences( (PartialPropertySource) source );
			break;
		case SECURITY:
			convertor = new SecurityPreferences( (PartialPropertySource) source );
			break;
		default:
			break;
		}
		return convertor;
	}

	@Override
	public IJp2pHandler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}
}
