package net.jp2p.container.context;

import org.xml.sax.Attributes;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.factory.IJp2pComponents;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.log.LoggerFactory;
import net.jp2p.container.partial.PartialFactory;
import net.jp2p.container.persistence.SimplePersistenceFactory;
import net.jp2p.container.properties.AbstractJp2pPropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.properties.IJp2pDirectives.Contexts;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.container.properties.SimplePropertyConvertor;
import net.jp2p.container.startup.StartupServiceFactory;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.container.utils.Utils;

public class Jp2pContext implements IJp2pContext<Object> {

	public static enum Components implements IJp2pComponents{
		JP2P_CONTAINER,
		CONTEXT,
		STARTUP_SERVICE,
		PERSISTENCE_SERVICE,
		SECURITY,
		TCP,
		HTTP,
		HTTP2,
		MULTICAST,
		LOGGER_SERVICE;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
		
		public static boolean isComponent( String str ){
			str = StringStyler.styleToEnum(str);
			if(( str == null ) || ( str.length() == 0 ))
				return false;
			for( Components comp: values()){
				if( comp.name().equals( str.toUpperCase() ))
					return true;
			}
			return false;
		}
	}

	public Jp2pContext() {
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
		if( !Utils.isNull( contextName ) && !isContextNameEqual(Contexts.JP2P.toString(), contextName ))
			return false;
		return Components.isComponent( componentName );
	}

	/**
	 * Change the factory to a Chaupal factory if required and available
	 * @param factory
	 * @return
	 */
	@Override
	public IPropertySourceFactory<?> getFactory( IContainerBuilder builder, Attributes attributes, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		IPropertySourceFactory<?> factory = getDefaultFactory( builder, parentSource, componentName);
		return factory;
	}

	/**
	 * Returns true if the lowercases of the Strings are equAL
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isContextNameEqual( String str1, String str2 ){
		if( Utils.isNull(str1 ) && !Utils.isNull(str2))
			return false;
		if( Utils.isNull(str2 ) && !Utils.isNull(str1))
			return false;
		if( Utils.isNull(str1 ) && Utils.isNull(str2))
			return false;
		return str1.toLowerCase().equals( str2.toLowerCase());				
	}

	/**
	 * Get the context name by parsing the predecessors of the given source
	 * @param source
	 * @return
	 */
	public static String getContextName( IJp2pPropertySource<?> source ){
		String contextName = AbstractJp2pPropertySource.findFirstAncestorDirective(source, Directives.CONTEXT );
		if( Utils.isNull( contextName ))
			contextName = Contexts.JP2P.toString();
		return contextName;
	}

	/**
	 * Get the default factory for this container
	 * @param parent
	 * @param componentName
	 * @return
	 */
	public IPropertyConvertor<String, Object> getConvertor( IJp2pWritePropertySource<IJp2pProperties> source ){
		String comp = StringStyler.styleToEnum( source.getComponentName());
		if( !Components.isComponent( comp ))
			return getConvertor(source);
		Components component = Components.valueOf(comp);
		IPropertyConvertor<String, Object> convertor = null;
		switch( component ){
		default:
			convertor = new SimplePropertyConvertor( source );
			break;
		}
		return convertor;
	}

	/**
	 * Create a value for the given component name and id
	 * @param parent
	 * @param componentName
	 * @return
	 */
	@Override
	public Object createValue( String componentName, IJp2pProperties id ){
		return id.toString();
	}

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.builder.IContainerBuilder#getDefaultFactory(net.osgi.jp2p.properties.IJp2pPropertySource, java.lang.String)
	*/
	public static IPropertySourceFactory<?> getDefaultFactory( IContainerBuilder builder, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		if( Utils.isNull(componentName))
			return null;
		String comp = StringStyler.styleToEnum(componentName);
		if( !Components.isComponent( comp ))
			return null;
		Components component = Components.valueOf(comp);
		IPropertySourceFactory<?> factory = null;
		switch( component ){
		case STARTUP_SERVICE:
			factory = new StartupServiceFactory( builder, parentSource );
			break;
		case PERSISTENCE_SERVICE:
			factory = new SimplePersistenceFactory(builder, parentSource);
			break;
		case TCP:
		case HTTP:
		case HTTP2:
		case MULTICAST:
		case SECURITY:
			factory = new PartialFactory<Object>( builder, componentName, parentSource );
			break;
		case LOGGER_SERVICE:
			factory = new LoggerFactory( builder, parentSource );
			break;
		default:
			break;
		}
		return factory;
	}
}
