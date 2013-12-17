package net.osgi.jxse.pipe;

import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class PipePropertySource extends AbstractJxseWritePropertySource<IJxseProperties> {

	public enum PipeProperties implements IJxseProperties{
		PIPE_ID,
		TYPE;
	
		public static boolean isValidProperty( String str ){
			if( Utils.isNull( str ))
				return false;
			for( PipeProperties dir: values() ){
				if( dir.name().equals( str ))
					return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public enum PipeDirectives implements IJxseDirectives{
		PEERGROUP;
	
		public static boolean isValidDirective( String str ){
			if( Utils.isNull( str ))
				return false;
			for( PipeDirectives dir: values() ){
				if( dir.name().equals( str ))
					return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public PipePropertySource( IJxsePropertySource<?> parent) {
		super( Components.PIPE_SERVICE.toString(), parent);
	}

	
	@Override
	public IJxseDirectives getDirectiveFromString( String str ) {
		if( Utils.isNull(str))
			return null;
		if(!PipeDirectives.isValidDirective(str) )
			return super.getDirectiveFromString( str );
		return PipeDirectives.valueOf( str );
	}


	@Override
	public IJxseProperties getIdFromString(String key) {
		if( PipeProperties.isValidProperty(key))
			return PipeProperties.valueOf(key);
		return null;
	}

	@Override
	public boolean validate(IJxseProperties id, Object value) {
		return PipeProperties.isValidProperty(id.toString());	
	}	
}