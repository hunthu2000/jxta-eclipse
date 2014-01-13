package net.osgi.jp2p.jxta.advertisement;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class AdvertisementPropertySource extends AbstractJp2pWritePropertySource {

	/**
	 * The scope of an advertisement determines whether it will be published or not
	 * @author keesp
	 *
	 */
	public enum Scope{
		INTERNAL,
		LOCAL,
		REMOTE,
		REMOTE_ONLY;
	
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


	public enum AdvertisementProperties implements IJp2pProperties{
		NAME,
		MODE,
		SCOPE,
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

	public enum AdvertisementDirectives implements IJp2pDirectives{
		PEERGROUP,
		TYPE;
	
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
		ADVERTISEMENT,
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

	public AdvertisementPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( JxtaComponents.ADVERTISEMENT.toString(), parent);
		this.fillDefaultValues( parent );
	}

	public AdvertisementPropertySource(String componentName, IJp2pPropertySource<IJp2pProperties> parent) {
		super(componentName, parent);
		this.fillDefaultValues( parent);
	}

	protected void fillDefaultValues( IJp2pPropertySource<IJp2pProperties> parent ){
		super.setDirective(Directives.BLOCK_CREATION, Boolean.TRUE.toString());
		super.setDirective(AdvertisementDirectives.TYPE, parent.getDirective( AdvertisementDirectives.TYPE ));
		super.setDirective(AdvertisementDirectives.PEERGROUP, parent.getDirective( AdvertisementDirectives.PEERGROUP ));
		super.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( AdvertisementProperties.LIFE_TIME, PeerGroup.DEFAULT_LIFETIME ));
		super.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( AdvertisementProperties.EXPIRATION, PeerGroup.DEFAULT_EXPIRATION ));	
		super.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( AdvertisementProperties.MODE, AdvertisementMode.DISCOVERY_AND_PUBLISH, true ));
		super.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( AdvertisementProperties.SCOPE, Scope.REMOTE ));
	}
	
	@Override
	public boolean setDirective(IJp2pDirectives id, String value) {
		if( AdvertisementDirectives.isValidDirective( id.name()))
			return super.setDirective(AdvertisementDirectives.valueOf( id.name()), value );
		return super.setDirective(id, value);
	}

	@Override
	public IJp2pProperties getIdFromString(String key) {
		if( AdvertisementProperties.isValidProperty(key))
			return AdvertisementProperties.valueOf(key);
		return null;
	}

	@Override
	public boolean validate( IJp2pProperties id, Object value) {
		return AdvertisementProperties.isValidProperty( id.toString());
	}

	@Override
	public boolean addChild(IJp2pPropertySource<?> child) {
		if( !AdvertisementCategories.isValidCategory( StringStyler.styleToEnum( child.getComponentName() )))
			return false;
		return super.addChild(child);
	}
	
	/**
	 * Get the scope of the given source and present a default value when it is null;
	 * @param source
	 * @return
	 */
	public static Scope getScope( IJp2pPropertySource<IJp2pProperties> source ){
		Scope scope = (Scope) source.getProperty( AdvertisementProperties.SCOPE );
		if( scope == null )
			return Scope.REMOTE;
		else
			return scope;
		
	}
}
