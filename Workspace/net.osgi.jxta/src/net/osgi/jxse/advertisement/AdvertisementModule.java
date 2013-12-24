package net.osgi.jxse.advertisement;

import org.xml.sax.Attributes;

import net.jxta.document.Advertisement;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementDirectives;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementTypes;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class AdvertisementModule extends AbstractJxseModule<Advertisement, AdvertisementPropertySource> {

	private AdvertisementTypes type;
	
	public AdvertisementModule(  AdvertisementTypes type ) {
		this.type = type;
	}

	public AdvertisementModule(  AdvertisementTypes type, IJxseModule<?> parent) {
		super(parent);
		this.type = type;
	}

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}

	@Override
	protected AdvertisementPropertySource onCreatePropertySource() {
		AdvertisementPropertySource source = new AdvertisementPropertySource( super.getParent().getPropertySource() );
		return source;
	}

	public IComponentFactory<Advertisement> onCreateFactory( ) {
		return new JxseAdvertisementFactory( super.getPropertySource() );
	}

	/**
	 * Get the correct advertisement type
	 * @param attrs
	 * @param qName
	 * @param parent
	 * @return
	 */
	public static AdvertisementTypes getAdvertisementType( Attributes attrs, String qName ,IJxsePropertySource<IJxseProperties> parent ){
		if(( attrs == null ) || ( attrs.getLength() == 0))
				return AdvertisementTypes.ADV;
		String type = attrs.getValue(AdvertisementDirectives.TYPE.toString().toLowerCase() );
		if( Utils.isNull(type))
			return AdvertisementTypes.ADV;
		return AdvertisementTypes.valueOf( StringStyler.styleToEnum( type ));
	}	

	/**
	 * Get the correct property source
	 * @param attrs
	 * @param qName
	 * @param parent
	 * @return
	 */
	protected AdvertisementPropertySource getAdvertisementPropertysource( Attributes attrs, String qName ,IJxsePropertySource<IJxseProperties> parent ){
		AdvertisementPropertySource source = new AdvertisementPropertySource( qName, parent );
		AdvertisementTypes adv_type = this.getAdvertisementType(attrs, qName, parent);
		//switch( adv_type ){
		//case PIPE:
		//	return new PipePropertySource( source );
		//default:
			return source;
		//}
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
