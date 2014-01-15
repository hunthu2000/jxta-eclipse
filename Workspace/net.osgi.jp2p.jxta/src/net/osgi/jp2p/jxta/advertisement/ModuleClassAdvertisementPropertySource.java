package net.osgi.jp2p.jxta.advertisement;

import java.net.URISyntaxException;

import net.jxta.document.AdvertisementFactory;
import net.jxta.protocol.ModuleClassAdvertisement;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jp2p.jxta.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jp2p.jxta.advertisement.ModuleImplAdvertisementPropertySource.ModuleImplProperties;
import net.osgi.jp2p.jxta.advertisement.service.AdvertisementServicePropertySource;
import net.osgi.jp2p.jxta.factory.IJxtaComponentFactory.JxtaComponents;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.properties.IJp2pWritePropertySource;
import net.osgi.jp2p.properties.ManagedProperty;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

public class ModuleClassAdvertisementPropertySource extends AdvertisementServicePropertySource{
	
	/**
	 * Properties specific for module class services
	 * @author Kees
	 *
	 */
	public enum ModuleClassProperties implements IJp2pProperties{
		DESCRIPTION,
		MODULE_CLASS_ID;
	
		public static boolean isValidProperty( String str ){
			if( Utils.isNull( str ))
				return false;
			for( ModuleClassProperties dir: values() ){
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

	public ModuleClassAdvertisementPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( JxtaComponents.ADVERTISEMENT.toString(), parent);
		this.fillDefaultValues();
	}

	protected void fillDefaultValues( ) {
		this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( AdvertisementProperties.ADVERTISEMENT_TYPE, AdvertisementTypes.MODULE_SPEC ));
		String name = super.getParent().getDirective( Directives.NAME );
		if(!Utils.isNull( name )){
			this.setManagedProperty( new ManagedProperty<IJp2pProperties, Object>( ModuleImplProperties.CODE, name ));
		}
		String description = (String) super.getParent().getProperty( ModuleImplProperties.DESCRIPTION );
		if(!Utils.isNull( description )){
			super.setProperty( ModuleImplProperties.DESCRIPTION, description );
		}
	}

	@Override
	public IJp2pProperties getIdFromString(String key) {
		if( ModuleClassProperties.isValidProperty(key))
			return ModuleClassProperties.valueOf(key);
		return super.getIdFromString(key);
	}

	@Override
	public boolean validate(IJp2pProperties id, Object value) {
		return ModuleClassProperties.isValidProperty(id.toString());	
	}
	
	/**
	 * Create a module class advertisement
	 * @return
	 * @throws URISyntaxException 
	 */
	public static ModuleClassAdvertisement createModuleClassAdvertisement( IJp2pPropertySource<IJp2pProperties> source ) throws URISyntaxException{
		AdvertisementTypes type = AdvertisementTypes.valueOf( StringStyler.styleToEnum( (String) source.getDirective( AdvertisementDirectives.TYPE )));
		ModuleClassAdvertisementPreferences preferences = new ModuleClassAdvertisementPreferences( (IJp2pWritePropertySource<IJp2pProperties>) source );
		ModuleClassAdvertisement mcad = ( ModuleClassAdvertisement )AdvertisementFactory.newAdvertisement( AdvertisementTypes.convertTo(type));
		String name = (String) source.getProperty( AdvertisementServiceProperties.NAME );
		mcad.setName(name);
		mcad.setDescription(( String )source.getProperty( ModuleClassProperties.DESCRIPTION ));
		mcad.setModuleClassID( preferences.getModuleClassID());
		return mcad;
	}		
}