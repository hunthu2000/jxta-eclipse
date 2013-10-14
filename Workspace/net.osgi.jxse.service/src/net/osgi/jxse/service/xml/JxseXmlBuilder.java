package net.osgi.jxse.service.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.osgi.jxse.properties.CategoryPropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class JxseXmlBuilder<T extends Enum<T>, U extends IJxseDirectives> {

	public static final String DOC_HEAD = "<?xml version='1.0' encoding='UTF-8'?>\n";
	public static final String DOC_PROPERTY = "<properties>\n";
	public static final String DOC_PROPERTY_END = "</properties>\n";
	public static final String DOC_DIRECTIVE = "<directives>\n";
	public static final String DOC_DIRECTIVE_END = "</directives>\n";
	
	public JxseXmlBuilder() {
		super();
	}

	@SuppressWarnings("unchecked")
	public final String build( IJxsePropertySource<T,U> source ){
		StringBuffer buffer = new StringBuffer();
		buffer.append( DOC_HEAD);
		buildSource( 0, buffer, ( IJxsePropertySource<Enum<?>,IJxseDirectives> )source );
		return buffer.toString();
	}
	
	/**
	 * Build the context
	 * @param source
	 */
	@SuppressWarnings("unchecked")
	protected static void buildSource( int offset, StringBuffer buffer, IJxsePropertySource<Enum<?>,IJxseDirectives> source ){
		String component = StringStyler.xmlStyleString( source.getComponentName());
		buffer.append( createComponent( offset, component, source ));
		offset +=2;
		String str = createProperties( offset, source );
		if( !Utils.isNull( str ))
			buffer.append(str );
		for( IJxsePropertySource<?,?> child: source.getChildren()){
			if( !( child instanceof CategoryPropertySource ))
				buildSource( offset, buffer, ( IJxsePropertySource<Enum<?>,IJxseDirectives>)child );
		}
		offset-=2;
		buffer.append( insertOffset( offset ));
		buffer.append( xmlEndTag( component ));
	}

	/**
	 * Create the component
	 * @param offset
	 * @param source
	 * @return
	 */
	private static final String createComponent( int offset, String component, IJxsePropertySource<Enum<?>,IJxseDirectives> source ){
		StringBuffer buffer = new StringBuffer();
		Map<String,String> directives = new HashMap<String, String>();
		if(!Utils.isNull( source.getId() ))
			directives.put( IJxseDirectives.Directives.ID.toString().toLowerCase(), source.getId() );
		if(!Utils.isNull( source.getIdentifier() ))
			directives.put( IJxseDirectives.Directives.NAME.toString().toLowerCase(), source.getIdentifier() );
		Iterator<?> iterator = source.directiveIterator();
		IJxseDirectives key;
		Object value;
		while( iterator.hasNext() ){
			key = (IJxseDirectives) iterator.next();
			value = source.getDirective(key);
			if(( value != null ) && ( !Utils.isNull( value.toString() ))){
				directives.put(key.toString(), value.toString());
			}
		}
		buffer.append( xmlBeginTag( offset, component, directives, true ));
		return buffer.toString();
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final String createProperties( int offset, IJxsePropertySource source ){
		StringBuffer buffer = new StringBuffer();
		buffer.append( insertOffset( offset));
		buffer.append( DOC_PROPERTY );
		Iterator<Enum> iterator = source.propertyIterator();
		boolean properties = false;
		while( iterator.hasNext() ){
			ManagedProperty property = source.getManagedProperty( iterator.next() );
			String str = createProperty( offset + 2, property);
			if( !Utils.isNull( str )){
				buffer.append( str );
				properties = true;
			}
		}
		for( IJxsePropertySource<?,?> child: source.getChildren()){
			if( child instanceof CategoryPropertySource ){
				properties = true;
				buildSource( offset, buffer,  ( IJxsePropertySource<Enum<?>,IJxseDirectives>)child );
			}
		}
		if( !properties )
			return null;
		buffer.append( insertOffset( offset));
		buffer.append( DOC_PROPERTY_END );
		return buffer.toString();
	}
	
	private static String createProperty( int offset, ManagedProperty<?, IJxseDirectives> property ){
		if(( property == null ) || property.isDerived() || ( property.getValue() == null ))
			return null;
		StringBuffer buffer = new StringBuffer();
		buffer.append( xmlBeginTag( offset, toXmlStyle( property.getKey() ), property.getAttributes(), false));
		buffer.append( property.getValue() );
		buffer.append( xmlParseEndTag( offset, toXmlStyle( property.getKey() )));	
		return buffer.toString();
	}

	/**
	 * Create the attributes from the managed property
	 * @param property
	 * @return
	 */
	private static String createAttributes( Map<String, String> attributes ){
		if(( attributes == null ) || ( attributes.size() == 0 ))
			return null;
		StringBuffer buffer = new StringBuffer();
		String value;
		for( String attr: attributes.keySet()){
			value = attributes.get( attr );
			if( Utils.isNull( attr ) || Utils.isNull( value ))
				continue;
			buffer.append( attr + "=\"" + value + "\" ");
		}
		return buffer.toString();
	}

	/**
	 * Replace the given enum the str
	 * @param enm
	 * @return
	 */
	private static String toXmlStyle( Object enm ){
		String str = enm.toString();
		if( enm instanceof Enum<?> ){
			Enum<?> nm = ( Enum<?> )enm;
			str = nm.name().toLowerCase();
		}
		str = str.replace("_", "-").trim();
		return str;
	}
	
	/**
	 * Get the leading spaces for this source
	 * @param source
	 * @return
	 */
	private static String insertOffset( int offset ){
		StringBuffer buffer = new StringBuffer();
		for( int i=0; i<offset; i++ )
			buffer.append(" ");
		return buffer.toString();
	}
	
	/**
	 * Create an XML begin tag for the given string 
	 * @param str
	 * @return
	 */
	public static String xmlBeginTag( int offset, String str, Map<String, String> attributes, boolean eol ){
		String attrStr = createAttributes(attributes);
		if( Utils.isNull( attrStr ))
			return xmlParseBeginTag( offset, str, eol );
		else{
			return xmlParseBeginTag( offset, str + " " + attrStr, eol );
		}
	}

	private static String xmlParseBeginTag( int offset, String str, boolean eol ){
		StringBuffer buffer = new StringBuffer();
		String[] split = str.split("-8");
		if( split.length == 1 ){
			buffer.append( insertOffset( offset ));
			buffer.append( xmlBeginTag( str, eol ));
			return buffer.toString();
		}
		for( int i=0; i<split.length; i++ ){
			String line = split[i];
			if( Utils.isNull(line))
				continue;
			buffer.append( insertOffset( offset + 2*i  ));
			buffer.append( xmlBeginTag( line, i < split.length - 1 ));
		}
		return buffer.toString();
	}

	/**
	 * Create an XML begin tag for the given string 
	 * @param str
	 * @return
	 */
	public static String xmlBeginTag( String str, boolean eol ){
		String newstr = "<" + str + ">";
		if( eol )
			newstr += "\n";
		return newstr;
	}

	/**
	 * Create an XML end tag for the given string 
	 * @param str
	 * @return
	 */
	private static String xmlEndTag( String str ){
		return "</" + str + ">\n";
	}

	private static String xmlParseEndTag( int offset, String str ){
		StringBuffer buffer = new StringBuffer();
		String[] split = str.split("-8");
		if( split.length == 1 ){
			buffer.append(xmlEndTag( str ));
			return buffer.toString();
		}
		for( int i = split.length-1; i>=0; i-- ){
			String line = split[i];
			if( Utils.isNull( line ))
				continue;
			buffer.append( xmlEndTag( line ));
			if( i > 0 )
				buffer.append( insertOffset( offset + 2*(i-1)));
		}
		return buffer.toString();
	}

}
