package net.jp2p.endpoint.servlethttp.context;

import org.xml.sax.Attributes;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.context.IJp2pContext;
import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.utils.Utils;
import net.jp2p.container.xml.IJp2pHandler;
import net.jp2p.jxta.network.configurator.partial.PartialNetworkConfigFactory;

public class HttpContext implements IJp2pContext {

	public static final String S_HTTP_CONTEXT = "HttpContext";
	public static final String S_HTTP_SERVICE = "HttpService";
	
	public HttpContext() {
	}

	@Override
	public String getName() {
		return S_HTTP_CONTEXT;
	}

	/**
	 * Returns true if the given component name is valid for this context
	 * @param componentName
	 * @return
	 */
	public boolean isValidComponentName( String contextName, String componentName ){
		if( !Utils.isNull( contextName ) && !Jp2pContext.isContextNameEqual( S_HTTP_CONTEXT, contextName ))
			return false;
		return S_HTTP_SERVICE.equals( componentName );
	}

	/**
	 * Change the factory to a Chaupal factory if required and available
	 * @param factory
	 * @return
	 */
	@Override
	public IPropertySourceFactory<?> getFactory( IContainerBuilder builder, Attributes attributes, IJp2pPropertySource<IJp2pProperties> parentSource, String componentName ){
		if( !isValidComponentName( S_HTTP_CONTEXT, componentName))
			return null;
		IPropertySourceFactory<?> factory = new PartialNetworkConfigFactory<Object>( builder, componentName, parentSource );
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
		return null;
	}

	@Override
	public IJp2pHandler getHandler() {
		// TODO Auto-generated method stub
		return null;
	}

}
