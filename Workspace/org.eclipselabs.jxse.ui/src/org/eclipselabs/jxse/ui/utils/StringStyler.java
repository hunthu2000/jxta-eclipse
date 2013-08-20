package org.eclipselabs.jxse.ui.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringStyler 
{

	/**
	 * Utility inserts a space before every caps in a string  
	 * @param str
	 * @return
	 */
	public static String insertSpace( String str )
	{
  	String chr;
  	StringBuffer buffer = new StringBuffer();
  	for( int i = 0; i < str.length(); i++ ){
  		chr = String.valueOf( str.charAt( i ));
  		if(( i > 0 ) && ( chr.matches( "[A-Z]")))
  			buffer.append( " "); 
  		buffer.append( chr );
  	}
  	return buffer.toString();		
	}
	
	/**
	 * Places the insertString before the sequences matching the
	 * given regular expression.
	 * 
	 * @param value
	 * @param insertString
	 * @param regex
	 * @return
	*/
	public static String insertAll( String value, String insertString, String regex )
	{
		Pattern pattern = Pattern.compile( regex );
    StringBuffer buf = new StringBuffer( value );
    Matcher matcher = pattern.matcher( value );
    int start = 0;
    int count = 0;
    while (matcher.find( start )) {
    	start = matcher.end() + 1;
      buf.insert( start - 2 + count, "\\" );
      if( start >= buf.length() )
      	break;
    	if(( matcher.end() - matcher.start() )%2 == 0)
      	continue;
      start = matcher.end() + 1;
      count++;
    } 
    return buf.toString();
	}

	/**
	 * Set the first character of the string to uppercase
	 * @param strng
	 * @return
	 */
	public static String firstUpperCaseString( String strng ){
		char chr = strng.charAt(0);
		String str = strng.toString().toLowerCase().substring(1);
		return String.valueOf(chr) + str;		
	}
	
	/**
	 * Create a pretty string from the given one
	 * @param strng
	 * @return
	 */
	public static String prettyString( String strng ){
		strng = strng.replaceAll("_8", ".");
		strng = strng.replaceAll(" ", "_");
		String[] split = strng.split("[_]");
		StringBuffer buffer = new StringBuffer();
		for( String str: split ){
			buffer.append( firstUpperCaseString( str ));
		}
		return buffer.toString();
	}
}
