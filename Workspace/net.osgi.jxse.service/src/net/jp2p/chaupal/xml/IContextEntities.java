package net.jp2p.chaupal.xml;

import net.jp2p.container.utils.StringStyler;

public interface IContextEntities {

	public enum Groups{
		PROPERTIES,
		DIRECTIVES;

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

}
