package net.osgi.jxse.advertisement;

import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class AdvertisementPropertySource extends AbstractJxseWritePropertySource<IJxseProperties> {

	public static String S_ADVERTISEMENTS = "Adverisements";
	
	/**
	 * The scope of an advertisement determines whter it will be published or not
	 * @author keesp
	 *
	 */
	public enum Scope{
		INTERNAL,
		LOCAL,
		REMOTE;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public enum AdvertisementDirectives implements IJxseDirectives{
		ADVERTISEMENT_TYPE,
		SCOPE;
	
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
		super( S_ADVERTISEMENTS, parent);
		super.setDirective(AdvertisementDirectives.SCOPE, Scope.REMOTE);
	}

	public AdvertisementPropertySource(String componentName,
			IJxsePropertySource<?, IJxseDirectives> parent) {
		super(componentName, parent);
		super.setDirective(AdvertisementDirectives.SCOPE, Scope.REMOTE);
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

	@Override
	public Object getDefaultDirectives(IJxseDirectives id) {
		return null;
	}
}
