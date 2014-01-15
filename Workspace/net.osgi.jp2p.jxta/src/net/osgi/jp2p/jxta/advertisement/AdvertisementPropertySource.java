package net.osgi.jp2p.jxta.advertisement;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public class AdvertisementPropertySource extends AbstractJp2pWritePropertySource {

	public enum AdvertisementTypes{
		ADV,
		PEERGROUP,
		PEER,
		MODULE_CLASS,
		MODULE_SPEC,
		MODULE_IMPL,
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
			case MODULE_IMPL:
				return ModuleImplAdvertisement.getAdvertisementType();
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

	public enum AdvertisementProperties implements IJp2pProperties{
		NAME,
		ADVERTISEMENT_TYPE;
	
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
	}
	

	@Override
	public boolean validate( IJp2pProperties id, Object value) {
		return true;
	}

	@Override
	public boolean addChild(IJp2pPropertySource<?> child) {
		if( !AdvertisementCategories.isValidCategory( StringStyler.styleToEnum( child.getComponentName() )))
			return false;
		return super.addChild(child);
	}
	
	/**
	 * Find the descendant advertisement with the given ad type.
	 * @param source
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static IJp2pPropertySource<IJp2pProperties> findAdvertisementDescendant( IJp2pPropertySource<IJp2pProperties> source, AdvertisementTypes type ){
		if(( type == null ) || ( !JxtaComponents.ADVERTISEMENT.equals( source.getComponentName() )))
			return null;
		AdvertisementTypes adtype = (AdvertisementTypes) source.getProperty( AdvertisementProperties.ADVERTISEMENT_TYPE );
		if( type.equals(adtype ))
			return (IJp2pPropertySource<IJp2pProperties>) source;
		for( IJp2pPropertySource<?> child: source.getChildren() ){
			return findAdvertisementDescendant((IJp2pPropertySource<IJp2pProperties>) child, adtype);
		}
		return null;
	}
}