package net.osgi.jp2p.properties;

import net.osgi.jp2p.utils.StringStyler;

public interface IJp2pDirectives{

	/**
	 * Directives give additional clues on how to create the component
	 * @author Kees
	 *
	 */
	public enum Directives implements IJp2pDirectives{
		XMLNS_1XS,
		SCHEMA_LOCATION,
		ID,
		NAME,
		CLASS,
		FILE,
		VISIBLE,
		CONTEXT,
		AUTO_START,
		CLEAR,
		VERSION,
		BLOCK_CREATION;

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

	/**
	 * The types of services that are available
	 * @author Kees
	 *
	 */
	public enum Contexts{
		JXTA,
		JXSE,
		CHAUPAL;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public String name();	
}
