package net.osgi.jxse.properties;

import net.osgi.jxse.utils.StringStyler;

public interface IJxseDirectives{

	/**
	 * Directives give additional clues on how to create the component
	 * @author Kees
	 *
	 */
	public enum Directives implements IJxseDirectives{
		XMLNS_1XS,
		SCHEMA_LOCATION,
		ID,
		NAME,
		AUTO_START,
		CLEAR_CONFIG,
		PEER_ID_PERSIST,
		PEER_ID_CREATE,
		CREATE_PARENT,
		ACTIVATE_PARENT;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static boolean isDirective( String str ){
			str = StringStyler.styleToEnum(str);
			if(( str == null ) || ( str.length() == 0 ))
				return false;
			for( Directives comp: values()){
				if( comp.name().equals( str.toUpperCase() ))
					return true;
			}
			return false;
		}
	}

	public String name();	
}
