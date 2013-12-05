package net.osgi.jxse.advertisement;

import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class PipeAdvertisementPropertySource extends
		AdvertisementPropertySource {

	public enum PipeAdvertisementProperties implements IJxseProperties{
		PIPE_ID,
		TYPE;
	
		public static boolean isValidProperty( String str ){
			if( Utils.isNull( str ))
				return false;
			for( PipeAdvertisementProperties dir: values() ){
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

	public PipeAdvertisementPropertySource(
			IJxsePropertySource<?, IJxseDirectives> parent) {
		super(parent);
	}

	public PipeAdvertisementPropertySource(String componentName,
			IJxsePropertySource<?, IJxseDirectives> parent) {
		super(componentName, parent);
	}

	@Override
	public IJxseProperties getIdFromString(String key) {
		if( PipeAdvertisementProperties.isValidProperty(key))
			return PipeAdvertisementProperties.valueOf(key);
		return super.getIdFromString(key);
	}

	@Override
	public boolean validate(IJxseProperties id, Object value) {
		if( super.validate(id, value))
			return true;
		return PipeAdvertisementProperties.isValidProperty(id.toString());	
	}	
}