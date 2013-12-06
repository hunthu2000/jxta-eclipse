package net.osgi.jxse.advertisement;

import net.jxta.document.Advertisement;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public class AdvertisementModule implements IJxseModule<Advertisement, IJxseProperties, IJxseDirectives> {

	private IJxsePropertySource<?,?> parentSource;
	private IJxseWritePropertySource<IJxseProperties, IJxseDirectives> source;
	private String name;
	private AdvertisementTypes type;

	public AdvertisementModule( String name, AdvertisementTypes type ) {
		this( name, type, null );
	}

	public AdvertisementModule( String name, AdvertisementTypes type, IJxseWritePropertySource<?,?> parentSource) {
		this.parentSource = parentSource;
		this.name = name;
		this.type = type;
		this.source = this.getAdvertisementPropertysource();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IJxsePropertySource<IJxseProperties, IJxseDirectives> getPropertySource() {
		return source;
	}

	@Override
	public void setProperty( IJxseProperties id, Object value ){
		this.source.setProperty(id, value);
	}

	@Override
	public IComponentFactory<Advertisement, IJxseProperties, IJxseDirectives> createFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get the correct property source
	 * @param attrs
	 * @param qName
	 * @param parent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected IJxseWritePropertySource<IJxseProperties, IJxseDirectives> getAdvertisementPropertysource(){
		AdvertisementPropertySource source = new AdvertisementPropertySource( this.getName(), (IJxsePropertySource<?, IJxseDirectives>) this.parentSource );
		switch( type){
		case PIPE:
			return new PipeAdvertisementPropertySource( source );
		default:
			return source;
		}
	}
}
