package net.osgi.jp2p.jxta.peergroup;

import java.net.URISyntaxException;

import net.jxta.document.AdvertisementFactory;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.ModuleImplAdvertisementPropertySource.ModuleImplProperties;
import net.osgi.jp2p.jxta.advertisement.service.AdvertisementServicePropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupProperties;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class PeerGroupAdvertisementPropertySource extends AdvertisementPropertySource{
	
	/**
	 * Properties specific for module spec services
	 * @author Kees
	 *
	 */
	public enum PeerGroupAdvertisementProperties implements IJp2pProperties{
		DESCRIPTION,
		TYPE,
		PIPE_ID;
	
		public static boolean isValidProperty( String str ){
			if( Utils.isNull( str ))
				return false;
			for( PeerGroupAdvertisementProperties dir: values() ){
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

	public PeerGroupAdvertisementPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( AdvertisementTypes.PEERGROUP, parent);
	}

	protected void fillDefaultValues( IJp2pPropertySource<IJp2pProperties> parent ) {
		super.fillDefaultValues(parent);
		String name = super.getParent().getDirective( Directives.NAME );
		if(Utils.isNull( name )){
			name = (String) super.getParent().getProperty( ModuleImplProperties.CODE );
		}
		if(!Utils.isNull( name )){
			this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( ModuleImplProperties.CODE, name ));
		}
	}

	@Override
	public IJp2pProperties getIdFromString(String key) {
		if( PeerGroupAdvertisementProperties.isValidProperty(key))
			return PeerGroupAdvertisementProperties.valueOf(key);
		return super.getIdFromString(key);
	}

	@Override
	public boolean validate(IJp2pProperties id, Object value) {
		return PeerGroupAdvertisementProperties.isValidProperty(id.toString());	
	}	
	
	/**
	 * Create a pipe advertisement
	 * @return
	 * @throws URISyntaxException 
	 */
	public static PeerGroupAdvertisement createPeerGroupAdvertisement( IJp2pPropertySource<IJp2pProperties> source, ModuleSpecAdvertisement msadv ) throws URISyntaxException{
		AdvertisementTypes type = AdvertisementTypes.convertFrom((String) source.getDirective( AdvertisementDirectives.TYPE ));
		PeerGroupAdvertisement pgad = ( PeerGroupAdvertisement )AdvertisementFactory.newAdvertisement( AdvertisementTypes.convertTo(type));
		String name = (String) source.getProperty( AdvertisementProperties.NAME );
		pgad.setName(name);
		pgad.setModuleSpecID( msadv.getModuleSpecID());
		pgad.setDescription(( String )source.getProperty( PeerGroupProperties.DESCRIPTION ));
		PeerGroupPreferences preferences = new PeerGroupPreferences((IJp2pWritePropertySource<IJp2pProperties>) source); 
		pgad.setPeerGroupID(preferences.getPeerGroupID());
		return pgad;
	}

	
}