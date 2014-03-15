package net.jp2p.chaupal.context;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.logging.Logger;

import net.jp2p.container.properties.ManagedProperty;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jp2p.container.utils.StringStyler;
import net.jp2p.container.utils.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class ServiceHandler extends DefaultHandler{

	public static final int MAX_COUNT = 200;
	
	public enum Groups{
		PROPERTIES,
		DIRECTIVES,
		SEED_LIST;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isGroup( String str ){
			str = StringStyler.styleToEnum(str);
			if(( str == null ) || ( str.length() == 0 ))
				return false;
			for( Groups comp: values()){
				if( comp.name().equals( str.toUpperCase() ))
					return true;
			}
			return false;
		}
	}
	
	private Stack<String> stack;
	private Collection<ServiceInfo> services;
	private boolean skip;

	public ServiceHandler( Collection<ServiceInfo> services ) {
		this.services = new ArrayList<ServiceInfo>();
		this.skip = false;
		this.stack = new Stack<String>();
	}

	/**
	 * Get the root factory 
	 * @return
	 */
	Collection<ServiceInfo> getServices() {
		return services;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, 
			Attributes attributes) throws SAXException {
		if(skip)
			return;
		
		//First heck for groups
		if( Groups.isGroup( qName )){
			stack.push( qName );
			skip = true;
			return;
		}
		ServiceInfo info = new ServiceInfo();
		info.context = attributes.getValue(Directives.CONTEXT.toString().toLowerCase());
		info.name = qName;
		services.add( info );
	}
		
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if( !stack.peek().equals( qName ))
			return;
		this.stack.pop();
		skip = false;
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		print(e);
		super.error(e);
	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException {
		print(arg0);
		super.fatalError(arg0);
	}

	@Override
	public void warning(SAXParseException arg0) throws SAXException {
		print(arg0);
		super.warning(arg0);
	}


	private MessageFormat message =
		      new MessageFormat("({0}: {1}, {2}): {3}");
	
	private void print(SAXParseException x)
	{
		String msg = message.format(new Object[]
				{
				x.getSystemId(),
				new Integer(x.getLineNumber()),
				new Integer(x.getColumnNumber()),
				x.getMessage()
				});
		Logger.getLogger( this.getClass().getName()).info(msg);
	}
	
	/**
	 * Returns true if one of the attributes allows creation of values
	 * @param attributes
	 * @return
	 */
	protected static boolean isCreated( Attributes attributes ){
		String attr = attributes.getValue( ManagedProperty.Attributes.PERSIST.name().toLowerCase() );
		if( Utils.isNull(attr))
			return false;
		return Boolean.parseBoolean( attr );
	}
}

class ServiceInfo{
	String name;
	String context;
	boolean foud;
}
