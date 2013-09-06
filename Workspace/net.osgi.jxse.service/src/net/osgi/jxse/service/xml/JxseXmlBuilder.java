package net.osgi.jxse.service.xml;

import net.osgi.jxse.component.IJxseComponent.ModuleProperties;
import net.osgi.jxse.context.IJxseServiceContext.ContextDirectives;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.preferences.properties.IJxsePropertySource;
import net.osgi.jxse.preferences.properties.JxseContextPropertySource;
import net.osgi.jxse.utils.Utils;

public class JxseXmlBuilder<T extends Enum<T>, U extends Enum<U>> {

	public static final String DOC_HEAD = "<?xml version='1.0' encoding='UTF-8'?>\n";
	public static final String DOC_PROPERTY = "<property>\n";
	public static final String DOC_PROPERTY_END = "</property>\n";
	public static final String DOC_DIRECTIVE = "<directive>\n";
	public static final String DOC_DIRECTIVE_END = "</directive>\n";
	
	private StringBuffer buffer;
	
	public JxseXmlBuilder() {
		super();
		buffer = new StringBuffer();
	}

	public final String build( IJxsePropertySource<T,U> source ){
		buffer.append( DOC_HEAD);
		build( buffer, source );
		return buffer.toString();
	}
	
	/**
	 * Build the context
	 * @param source
	 */
	protected void buildContext( IJxsePropertySource< ContextProperties, ContextDirectives> source ){
		buffer.append( getLeadingSpaces(source));
		
		String attributes = null;
		if( !Utils.isNull( source.getId()))
			attributes = ModuleProperties.ID.toString().toLowerCase() + "=" + source.getId();
		buffer.append( xmlBeginTag( source.getComponentName(), attributes, true));
		String str = null;
		buffer.append( getLeadingSpaces(source, 2));
		buffer.append( DOC_DIRECTIVE );
		for( ContextDirectives directive: ContextDirectives.values() ){
			if(( source.getDirective( directive ) == null ) || ( Utils.isNull( source.getDirective( directive ).toString() )))
				continue;
			str =  xmlBeginTag( toXmlStyle( directive ), false );
			str += source.getDirective( directive );
			str += xmlEndTag( toXmlStyle( directive ));

			if( !Utils.isNull( str )){
				buffer.append( getLeadingSpaces(source, 4));
				buffer.append( str );
			}
		}
		buffer.append( getLeadingSpaces(source, 2));
		buffer.append( DOC_DIRECTIVE_END );			

		str = null;
		buffer.append( getLeadingSpaces(source,2));
		buffer.append( DOC_PROPERTY );
		for( ContextProperties props: ContextProperties.values() ){
			if(( source.getProperty(props ) == null ) || ( Utils.isNull( source.getProperty( props ).toString() )))
				continue;
			str = xmlBeginTag( toXmlStyle( props ), false);
			str += source.getProperty( props );
			str += xmlEndTag( toXmlStyle( props ));
			if( !Utils.isNull( str )){
				buffer.append( getLeadingSpaces(source, 4));
				buffer.append( str );
			}
		}
		buffer.append( getLeadingSpaces(source,2));
		buffer.append( DOC_PROPERTY_END );
		buffer.append( getLeadingSpaces(source));
		buffer.append( xmlEndTag( source.getComponentName() ));
	}
	
	@SuppressWarnings("unchecked")
	protected void build( StringBuffer buffer, IJxsePropertySource<?,?> source){
		if( source instanceof JxseContextPropertySource )
			buildContext( (IJxsePropertySource<ContextProperties, ContextDirectives>) source );
		for( IJxsePropertySource<?,?> child: source.getChildren())
			this.build( buffer, child );
	}
	
	/**
	 * Replace the given enum the str
	 * @param enm
	 * @return
	 */
	private String toXmlStyle( Enum<?> enm ){
		String str = enm.name().toLowerCase();
		str = str.replace("_", "-");
		return str;
	}
	
	/**
	 * Get the leading spaces for this source
	 * @param source
	 * @return
	 */
	private String getLeadingSpaces( IJxsePropertySource<?,?> source){
		return getLeadingSpaces(source, 0);
	}

	/**
	 * Get the leading spaces for this source
	 * @param source
	 * @return
	 */
	private String getLeadingSpaces( IJxsePropertySource<?,?> source, int offset ){
		StringBuffer buffer = new StringBuffer();
		for( int i=0; i<source.getDepth() + offset; i++ )
			buffer.append(" ");
		return buffer.toString();
	}

	/**
	 * Create an XML begin tag for the given string 
	 * @param str
	 * @return
	 */
	public static String xmlBeginTag( String str, String attributes,  boolean eol ){
		if( Utils.isNull(attributes ))
			return xmlBeginTag( str, eol );
		else
			return xmlBeginTag( str + " " + attributes, eol );
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
	public static String xmlEndTag( String str ){
		return "<" + str + "/>\n";
	}

}