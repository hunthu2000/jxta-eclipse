package net.osgi.jxse.service.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.properties.PartialPropertySource;
import net.osgi.jxse.properties.PropertySourceUtils;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class JxseXmlBuilder<T extends Enum<T>, U extends IJxseDirectives> {

	public static final String DOC_HEAD = "<?xml version='1.0' encoding='UTF-8'?>\n";
	public static final String DOC_PROPERTY = "<properties>\n";
	public static final String DOC_PROPERTY_END = "</properties>\n";
	public static final String DOC_DIRECTIVE = "<directives>\n";
	public static final String DOC_DIRECTIVE_END = "</directives>\n";
	
	public JxseXmlBuilder() {
	}

	@SuppressWarnings("unchecked")
	public final String build( IJxsePropertySource<T,U> source ){
		System.err.println( PropertySourceUtils.printPropertySource( source, true ));
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
		IJxsePropertySource<Enum<?>,IJxseDirectives> expand = PartialPropertySource.expand(source );
		String component = StringStyler.xmlStyleString( expand.getComponentName());
		buffer.append( createComponent( offset, component, expand ));
		offset +=2;
		String str = createProperties( offset, expand );
		if( !Utils.isNull( str ))
			buffer.append(str );
		for( IJxsePropertySource<?,?> child: expand.getChildren()){
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
		int newOffset = offset + 2;
		while( iterator.hasNext() ){
			ManagedProperty property = source.getManagedProperty( iterator.next() );
			String str = ( source instanceof PartialPropertySource )? createPartialProperty( newOffset, property): createProperty( newOffset, property);
			if( !Utils.isNull( str )){
				buffer.append( str );
				properties = true;
			}
		}
		for( IJxsePropertySource<?,?> child: source.getChildren()){
			buildSource( offset, buffer,  ( IJxsePropertySource<Enum<?>,IJxseDirectives>)child );
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
		String key = toXmlStyle( property.getKey() );
		buffer.append( xmlBeginTag( offset, key, property.getAttributes(), false));
		buffer.append( property.getValue() );
		buffer.append( xmlParseEndTag( offset, key ));	
		return buffer.toString();
	}

	private static String createPartialProperty( int offset, ManagedProperty<?, IJxseDirectives> property ){
		if(( property == null ) || property.isDerived() || ( property.getValue() == null ))
			return null;
		StringBuffer buffer = new StringBuffer();
		String key = toXmlStyle( property.getKey() );
		int index = key.indexOf("-8");
		if( index >= 0 )
			key = key.substring(index + 2, key.length());
		buffer.append( xmlBeginTag( offset, key , property.getAttributes(), false));
		buffer.append( property.getValue() );
		buffer.append( xmlParseEndTag( offset, key ));	
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
			buffer.append( StringStyler.xmlStyleString( attr ) + "=\"" + value + "\" ");
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
