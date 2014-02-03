package net.jp2p.container.context;

import org.xml.sax.Attributes;

import net.jp2p.container.builder.IContainerBuilder;
import net.jp2p.container.factory.IPropertySourceFactory;
import net.jp2p.container.properties.IJp2pDirectives;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.properties.IPropertyConvertor;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.container.xml.IJp2pHandler;

public interface IJp2pContext<T extends Object> {

	/**
	 * Directives give additional clues on how to create the component
	 * @author Kees
	 *
	 */
	public enum ContextDirectives implements IJp2pDirectives{
		NAME,
		CLASS;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isDirective( String str ){
			str = StringStyler.styleToEnum(str);
			if(( str == null ) || ( str.length() == 0 ))
				return false;
			for( ContextDirectives comp: values()){
				if( comp.name().equals( str.toUpperCase() ))
					return true;
			}
			return false;
		}
	}

	//The name of the context
	public String getName();
	
	/**
	 * Returns true if the given component name is valid for this context
	 * @param componentName
	 * @return
	 */
	public boolean isValidComponentName( String contextName, String componentName );
	
	//Get the factory that is created
	public IPropertySourceFactory<?> getFactory(IContainerBuilder builder,
			Attributes attributes,
			IJp2pPropertySource<IJp2pProperties> parentSource,
			String componentName);
	
	/**
	 * Get the handler for this context
	 * @return
	 */
	public IJp2pHandler getHandler();
	
	/**
	 * Get the default factory for this container
	 * @param parent
	 * @param componentName
	 * @return
	 */
	public IPropertyConvertor<String, Object> getConvertor( IJp2pWritePropertySource<IJp2pProperties> source );

	/**
	 * Create a value for the given component name and id
	 * @param parent
	 * @param componentName
	 * @return
	 */
	public Object createValue( String componentName, IJp2pProperties id );
}
