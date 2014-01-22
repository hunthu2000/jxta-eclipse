package net.jp2p.container.properties;

import java.util.Iterator;

public class PropertySourceUtils {

	/**
	 * Print the given property source
	 * @param source
	 * @param expand
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String printPropertySource( IJp2pPropertySource source, boolean expand ){
		if( source == null )
			return null;
		StringBuffer buffer = new StringBuffer();
		for( IJp2pPropertySource<Object> child: source.getChildren() )
			printPropertySource( buffer, child, expand);
		return buffer.toString();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void printPropertySource( StringBuffer buffer, IJp2pPropertySource<Object> source, boolean expand ){
		String offset = "";
		for( int i=0; i<source.getDepth(); i++ )
			offset += "\t";
		buffer.append( offset + "[" + source.getComponentName() + "]\n");
		if( expand ){
			buffer.append( offset + "\t{properties}\n");
			Iterator<Object> iterator = (Iterator<Object>) source.propertyIterator();
			while( iterator.hasNext() ){
				Object key = iterator.next(); 
				buffer.append( offset + "\t\t" + key.toString() + "=" + source.getProperty(key) + "\n");
			}
		}
		for( IJp2pPropertySource child: source.getChildren() )
			printPropertySource( buffer, child, expand);
	}

}
