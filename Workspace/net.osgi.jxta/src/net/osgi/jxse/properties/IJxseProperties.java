package net.osgi.jxse.properties;

import net.osgi.jxse.utils.StringStyler;

public interface IJxseProperties{

	/**
	 * Directives give additional clues on how to create the component
	 * @author Kees
	 *
	 */
	public enum JxseProperties implements IJxseProperties{
		STATUS,
		CREATE_DATE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isProperty( String str ){
			str = StringStyler.styleToEnum(str);
			if(( str == null ) || ( str.length() == 0 ))
				return false;
			for( JxseProperties comp: values()){
				if( comp.name().equals( str.toUpperCase() ))
					return true;
			}
			return false;
		}
	}

	public String name();	
}
