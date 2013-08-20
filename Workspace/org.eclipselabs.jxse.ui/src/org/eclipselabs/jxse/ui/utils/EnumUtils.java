package org.eclipselabs.jxse.ui.utils;

public class EnumUtils {

	/**
	 * Return a String representation of the given objects. Is used to
	 * quickly modify enums
	 * @param objects
	 * @return
	 */
	public static String[] toString( Object[] objects ){
		String[] results = new String[objects.length ];
		for( int i=0; i<objects.length; i++ )
			results[i] = objects[i].toString();
		return results;
	}
	
	//public String[] getValuesAsString( Enum enm ){
	//	for( )
	//}

}
