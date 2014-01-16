package net.osgi.jp2p.jxta.advertisement;

import java.net.URISyntaxException;

import net.jxta.document.AdvertisementFactory;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.osgi.jp2p.jxta.advertisement.service.AdvertisementServicePropertySource.AdvertisementDirectives;
import net.osgi.jp2p.jxta.peergroup.PeerGroupPropertySource.PeerGroupProperties;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.ManagedProperty;

public class ModuleImplAdvertisementPropertySource extends AdvertisementPropertySource{
	
	/**
	 * Properties specific for module spec services
	 * @author Kees
	 *
	 */
	public enum ModuleImplProperties implements IJp2pProperties{
		BASE_ADVERTISEMENT_TYPE,
		CODE,
		DESCRIPTION,
		MODULE_IMPL_ID,
		MODULE_SPEC_ID,
		PROVIDER,
		URI;
	
		public static boolean isValidProperty( String str ){
			if( Utils.isNull( str ))
				return false;
			for( ModuleImplProperties dir: values() ){
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

	public ModuleImplAdvertisementPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( AdvertisementTypes.MODULE_IMPL, parent);
	}

	protected void fillDefaultValues( IJp2pPropertySource<IJp2pProperties> parent ) {
		super.fillDefaultValues( parent);
		String name = super.getParent().getDirective( Directives.NAME );
		if(Utils.isNull( name )){
			name = (String) super.getParent().getProperty( PeerGroupProperties.PEERGROUP_NAME );
		}
			
		if(!Utils.isNull( name ))		
			this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( ModuleImplProperties.CODE, name ));
		String description = (String) super.getParent().getProperty( ModuleImplProperties.DESCRIPTION );
		if(!Utils.isNull( description )){
			super.setProperty( ModuleImplProperties.DESCRIPTION, description );
		}
	}

	@Override
	public IJp2pProperties getIdFromString(String key) {
		if( ModuleImplProperties.isValidProperty(key))
			return ModuleImplProperties.valueOf(key);
		return super.getIdFromString(key);
	}

	@Override
	public boolean validate(IJp2pProperties id, Object value) {
		return ModuleImplProperties.isValidProperty(id.toString());	
	}
	
	/**
	 * Create a module class advertisement
	 * @return
	 * @throws URISyntaxException 
	 */
	public static ModuleImplAdvertisement createModuleClassAdvertisement( IJp2pPropertySource<IJp2pProperties> source ) throws URISyntaxException{
		AdvertisementTypes type = AdvertisementTypes.valueOf( StringStyler.styleToEnum( (String) source.getDirective( AdvertisementDirectives.TYPE )));
		ModuleImplAdvertisement mcimpl = ( ModuleImplAdvertisement )AdvertisementFactory.newAdvertisement( AdvertisementTypes.convertTo(type));
		mcimpl.setModuleSpecID( (ModuleSpecID) source.getProperty( ModuleImplProperties.MODULE_SPEC_ID ));
		mcimpl.setCode(( String )source.getProperty( ModuleImplProperties.CODE ));
		mcimpl.setDescription(( String )source.getProperty( ModuleImplProperties.DESCRIPTION ));
		mcimpl.setProvider(( String )source.getProperty( ModuleImplProperties.PROVIDER ));
		mcimpl.setUri(( String )source.getProperty( ModuleImplProperties.URI ));
		return mcimpl;
	}	
}