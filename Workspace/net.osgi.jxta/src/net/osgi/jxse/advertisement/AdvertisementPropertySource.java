package net.osgi.jxse.advertisement;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class AdvertisementPropertySource extends AbstractJxseWritePropertySource<IJxseProperties> {

	public static String S_ADVERTISEMENTS = "AdvertisementService";
	
	/**
	 * The scope of an advertisement determines whether it will be published or not
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

	/**
	 * The mode gives clues on what to do with the service. either advertisements are only discovered,
	 * or they are (also) published
	 * @author Kees
	 *
	 */
	public enum AdvertisementMode{
		DISCOVERY,
		PUBLISH,
		DISCOVERY_AND_PUBLISH;

	@Override
	public String toString() {
		return StringStyler.prettyString( super.toString() );
	}}


	public enum AdvertisementProperties implements IJxseProperties{
		MODE,
		LIFE_TIME,
		EXPIRATION,
		DESCRIPTION;
	
		public static boolean isValidProperty( String str ){
			if( Utils.isNull( str ))
				return false;
			for( AdvertisementProperties dir: values() ){
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

	public enum AdvertisementDirectives implements IJxseDirectives{
		PEERGROUP,
		TYPE,
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

	public enum AdvertisementTypes{
		ADV,
		PEERGROUP,
		PEER,
		MODULE_CLASS,
		MODULE_SPEC,
		PIPE;
	
		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
		
		/**
		 * Convert the enum to a form that the jxta lib can understand
		 * @param adType
		 * @return
		 */
		public static String convert( AdvertisementTypes adType ){
			switch( adType ){
			case PEERGROUP:
				return PeerGroupAdvertisement.getAdvertisementType();
			case PIPE:
				return PipeAdvertisement.getAdvertisementType();
			case MODULE_CLASS:
				return ModuleClassAdvertisement.getAdvertisementType();
			case MODULE_SPEC:
				return ModuleSpecAdvertisement.getAdvertisementType();
			default:
				return Advertisement.getAdvertisementType();
			}
		}

		/**
		 * Convert the enum to a form that the jxta lib can understand
		 * @param adType
		 * @return
		 */
		public static int convertForDiscovery( AdvertisementTypes adType ){
			switch( adType ){
			case PEERGROUP:
				return DiscoveryService.GROUP;
			case PEER:
				return DiscoveryService.PEER;
			default:
				return DiscoveryService.ADV;
			}
		}

		/**
		 * Concert from advertisement type 
		*/
		public static AdvertisementTypes convertFrom( String tp ){
			if( Utils.isNull(tp))
				return AdvertisementTypes.ADV;
			String type = StringStyler.styleToEnum(tp);
			if( type.equals( PeerGroupAdvertisement.getAdvertisementType()))
				return AdvertisementTypes.PEERGROUP;
			if( type.equals(PipeAdvertisement.getAdvertisementType()))
				  return AdvertisementTypes.PIPE;
			if( type.equals(ModuleClassAdvertisement.getAdvertisementType()))
				  return AdvertisementTypes.MODULE_CLASS;
			if( type.equals(ModuleSpecAdvertisement.getAdvertisementType()))
				  return AdvertisementTypes.MODULE_SPEC;
			return AdvertisementTypes.ADV;
		}

		/**
		 * Concert from advertisement type 
		 */
		public static String convertTo( AdvertisementTypes advType ){
			switch( advType ){
			case PEERGROUP:
				return PeerGroupAdvertisement.getAdvertisementType();
			case PIPE:
				return PipeAdvertisement.getAdvertisementType();
			case MODULE_CLASS: 
				return ModuleClassAdvertisement.getAdvertisementType();
			case MODULE_SPEC:
				return ModuleSpecAdvertisement.getAdvertisementType();
			default:
				return null;
			}
		}
	}

	/**
	 * The categories that are included as children
	 * @author Kees
	 *
	 */
	public enum AdvertisementCategories{
		BODY,
		DISCOVERY_SERVICE;

		public static boolean isValidCategory( String category ){
			if( Utils.isNull( category ))
				return false;
			for( AdvertisementCategories cat: values() ){
				if( cat.name().equals( category ))
					return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	public AdvertisementPropertySource(IJxsePropertySource<?> parent) {
		super( S_ADVERTISEMENTS, parent);
		this.fillDefaultValues();
	}

	public AdvertisementPropertySource(String componentName,
			IJxsePropertySource<?> parent) {
		super(componentName, parent);
		this.fillDefaultValues();
	}

	protected void fillDefaultValues(){
		super.setDirective(AdvertisementDirectives.SCOPE, Scope.REMOTE.toString());
		super.setManagedProperty( new ManagedProperty<IJxseProperties, Object>( AdvertisementProperties.LIFE_TIME, PeerGroup.DEFAULT_LIFETIME ));
		super.setManagedProperty( new ManagedProperty<IJxseProperties, Object>( AdvertisementProperties.EXPIRATION, PeerGroup.DEFAULT_EXPIRATION ));	
		super.setManagedProperty( new ManagedProperty<IJxseProperties, Object>( AdvertisementProperties.MODE, AdvertisementMode.DISCOVERY, true ));
	}

	@Override
	public IJxseDirectives getDirectiveFromString(String id) {
		if(!AdvertisementDirectives.isValidDirective(id))
			return super.getDirectiveFromString(id);
		return AdvertisementDirectives.valueOf( id );
	}

	@Override
	public IJxseProperties getIdFromString(String key) {
		if( AdvertisementProperties.isValidProperty(key))
			return AdvertisementProperties.valueOf(key);
		return null;
	}

	@Override
	public boolean validate( IJxseProperties id, Object value) {
		return AdvertisementProperties.isValidProperty( id.toString());
	}

	@Override
	public boolean addChild(IJxsePropertySource<?> child) {
		if( !AdvertisementCategories.isValidCategory( StringStyler.styleToEnum( child.getComponentName() )))
			return false;
		return super.addChild(child);
	}
}
