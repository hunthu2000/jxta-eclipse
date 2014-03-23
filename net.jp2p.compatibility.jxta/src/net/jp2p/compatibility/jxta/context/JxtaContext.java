package net.jp2p.compatibility.jxta.context;

import org.xml.sax.Attributes;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.IJp2pDirectives.Contexts;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.container.utils.Utils;
import net.jp2p.container.xml.IJp2pHandler;
import net.jp2p.jxta.factory.JxtaFactoryUtils;
import net.jp2p.jxta.factory.IJxtaComponents.JxtaComponents;
import net.jp2p.jxta.network.NetworkManagerPreferences;
import net.jp2p.jxta.network.configurator.OverviewPreferences;
import net.jp2p.jxta.seeds.SeedListPropertySource;

public class JxtaContext implements IJp2pContext {

	public JxtaContext() {
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
		JxtaComponents[] components = JxtaComponents.values();
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
	public boolean isValidComponentName( String contextName, String componentName ){
		if( !Utils.isNull( contextName ) && !Jp2pContext.isContextNameEqual(Contexts.JXTA.toString(), contextName ))
			return false;
		return JxtaComponents.isComponent( componentName );
	}

	/**
	 * Change the factory to a Chaupal factory if required and available
	 * @param factory
	 * @return
	 */
	@Override
	public IPropertySourceFactory getFactory( IContainerBuilder builder, Attributes attributes, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		JxtaComponents component = JxtaComponents.valueOf( StringStyler.styleToEnum(componentName));
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
	public IPropertyConvertor<String, Object> getConvertor( IJp2pWritePropertySource<IJp2pProperties> source ){
		String comp = StringStyler.styleToEnum( source.getComponentName());
		if( !JxtaComponents.isComponent( comp ))
			return null;
		JxtaComponents component = JxtaComponents.valueOf(comp);
		IPropertyConvertor<String, Object> convertor = null;
		switch( component ){
		case NETWORK_MANAGER:
			convertor = new NetworkManagerPreferences( source );
			break;			
		case NETWORK_CONFIGURATOR:
			convertor = new OverviewPreferences( source );
			break;
		case SEED_LIST:
			SeedListPropertySource slps = (SeedListPropertySource) source;
			//SeedInfo seedInfo = new SeedInfo((( IJp2pProperties )source.getKey()).name(), ( String )value );
			//slps.setProperty( (IJp2pProperties) property.getKey(), seedInfo );
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
