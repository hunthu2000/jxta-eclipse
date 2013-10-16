package net.osgi.jxse.advertisement;

import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class AdvertisementPropertySource extends AbstractJxseWritePropertySource< IJxseProperties, IJxseDirectives> {

	public enum AdvertisementDirectives implements IJxseDirectives{
		ADVERTISEMENT_TYPE,
		PUBLISH_LOCAL,
		PUBLISH_REMOTE;
	
		public static boolean isValidDirective( String directive ){
			if( Utils.isNull( directive ))
				return false;
			for( AdvertisementDirectives dir: values() ){
				if( dir.name().equals( directive ))
					return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public AdvertisementPropertySource(IJxsePropertySource<?, IJxseDirectives> parent) {
		super(parent);
		super.setDirective(AdvertisementDirectives.PUBLISH_LOCAL, true);
	}

	public AdvertisementPropertySource(String componentName,
			IJxsePropertySource<?, IJxseDirectives> parent) {
		super(componentName, parent);
		super.setDirective(AdvertisementDirectives.PUBLISH_LOCAL, true);
	}

	@Override
	public IJxseDirectives getDirectiveFromString(String id) {
		if(!AdvertisementDirectives.isValidDirective(id))
			return super.getDirectiveFromString(id);
		return AdvertisementDirectives.valueOf( id );
	}

	@Override
	public IJxseProperties getIdFromString(String key) {
		return null;
	}

	@Override
	public boolean validate(IJxseProperties id, Object value) {
		return false;
	}
}
