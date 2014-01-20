package net.osgi.jp2p.jxta.pipe;

import java.net.URISyntaxException;

import net.jxta.document.AdvertisementFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource;
import net.osgi.jp2p.jxta.advertisement.ModuleImplAdvertisementPropertySource.ModuleImplProperties;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class PipeAdvertisementPropertySource extends AdvertisementPropertySource{
	
	/**
	 * Properties specific for module spec services
	 * @author Kees
	 *
	 */
	public enum PipeAdvertisementProperties implements IJp2pProperties{
		DESCRIPTION,
		TYPE,
		PIPE_ID;
	
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

	public PipeAdvertisementPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( AdvertisementTypes.PIPE, parent);
	}

	@Override
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
		if( PipeAdvertisementProperties.isValidProperty(key))
			return PipeAdvertisementProperties.valueOf(key);
		return super.getIdFromString(key);
	}

	@Override
	public boolean validate(IJp2pProperties id, Object value) {
		return PipeAdvertisementProperties.isValidProperty(id.toString());	
	}	
	
	/**
	 * Create a pipe advertisement
	 * @return
	 * @throws URISyntaxException 
	 */
	public static PipeAdvertisement createPipeAdvertisement( IJp2pPropertySource<IJp2pProperties> source, PeerGroup peergroup ) throws URISyntaxException{
		PipeAdvertisementPreferences preferences = new PipeAdvertisementPreferences( (IJp2pWritePropertySource<IJp2pProperties>) source, peergroup );
		PipeAdvertisement pipead = ( PipeAdvertisement )AdvertisementFactory.newAdvertisement( AdvertisementTypes.convertTo( AdvertisementTypes.PIPE ));
		String name = (String) source.getProperty( AdvertisementProperties.NAME );
		pipead.setName(name);
		pipead.setType(( String )source.getProperty( PipeAdvertisementProperties.TYPE ));
		pipead.setDescription(( String )source.getProperty( PipeAdvertisementProperties.DESCRIPTION ));
		pipead.setPipeID( preferences.getPipeID());
		return pipead;
	}

	
}