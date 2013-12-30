package org.eclipselabs.jxse.ui.property.advertisement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.jxta.document.Advertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleSpecAdvertisement;
import net.osgi.jxse.advertisement.AdvertisementPropertySource.AdvertisementProperties;
import net.osgi.jxse.component.IJxseComponent;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipselabs.jxse.ui.property.AbstractJxsePropertySource;
import org.eclipselabs.jxse.ui.property.advertisement.ModuleImplAdvPropertySource.ModuleImplAdvProperties;
import org.eclipselabs.jxse.ui.property.advertisement.ModuleSpecAdvPropertySource.ModuleSpecAdvProperties;

public class AdvertisementPropertySource extends AbstractJxsePropertySource<Advertisement> {

	public static final String S_DEFAULT_NAME = "Default JXTA Advertisement";

	public AdvertisementPropertySource( Advertisement source ) {
		super( source );
	}

	public AdvertisementPropertySource( IJxseComponent<Advertisement> component ) {
		super( component );
	}

	/**
	 * Provides an abstract description of the object, used for displays
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Advertisement advertisement = super.getModule();
		Collection<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		if( advertisement instanceof ModuleSpecAdvertisement ){
			ModuleSpecAdvPropertySource msaps = new ModuleSpecAdvPropertySource((ModuleSpecAdvertisement) advertisement );
			descriptors.addAll( Arrays.asList( msaps.getPropertyDescriptors()));
		}
		if( advertisement instanceof ModuleImplAdvertisement ){
			ModuleImplAdvPropertySource miaps = new ModuleImplAdvPropertySource((ModuleImplAdvertisement) advertisement );
			descriptors.addAll( Arrays.asList( miaps.getPropertyDescriptors()));
		}
		descriptors.addAll( Arrays.asList(super.getPropertyDescriptors( AdvertisementProperties.values())));
		descriptors.addAll( Arrays.asList(super.getPropertyDescriptors()));
		return descriptors.toArray( new IPropertyDescriptor[ descriptors.size() ]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		Advertisement advertisement = super.getModule();
		if( id instanceof ModuleSpecAdvProperties ){
			ModuleSpecAdvPropertySource msaps = new ModuleSpecAdvPropertySource((ModuleSpecAdvertisement) advertisement );
			return msaps.getPropertyValue(id);
		}
		if( id instanceof ModuleImplAdvProperties ){
			ModuleImplAdvPropertySource miaps = new ModuleImplAdvPropertySource((ModuleImplAdvertisement) advertisement );
			return miaps.getPropertyValue(id);
		}
		if(!( id instanceof AdvertisementProperties ))
			return super.getPropertyValue(id);
		
		AdvertisementProperties property = ( AdvertisementProperties )id;
		/**
		case ID:
			return advertisement.getID();
		case NAME:
			return S_DEFAULT_NAME;
		case ADVERTISEMENT_TYPE:
			return advertisement.getAdvType();
		case ADV_TYPE:
			return advertisement.getAdvType();
		}
	*/
		return super.getPropertyValue(id);
	}

	/**
	 * Returns true if the given property can be modified
	 * @param id
	 * @return
	 */
	@Override
	public boolean isEditable( Object id ){
		Advertisement advertisement = super.getModule();
		if( id instanceof ModuleSpecAdvProperties ){
			ModuleSpecAdvPropertySource msaps = new ModuleSpecAdvPropertySource((ModuleSpecAdvertisement) advertisement );
			return msaps.isEditable(id);
		}
		if( id instanceof ModuleImplAdvProperties ){
			ModuleImplAdvPropertySource miaps = new ModuleImplAdvPropertySource((ModuleImplAdvertisement) advertisement );
			return miaps.isEditable(id);
		}
		return false;
	}

	/**
	 * Currently not needed, there is no editing of properties
	 * @param id
	 * @param value
	 */
	@Override
	public void setPropertyValue(Object id, Object value){
		Advertisement advertisement = super.getModule();
		if( id instanceof ModuleSpecAdvProperties ){
			ModuleSpecAdvPropertySource msaps = new ModuleSpecAdvPropertySource((ModuleSpecAdvertisement) advertisement );
			msaps.setPropertyValue(id, value );
			return;
		}
		if( id instanceof ModuleImplAdvProperties ){
			ModuleImplAdvPropertySource miaps = new ModuleImplAdvPropertySource((ModuleImplAdvertisement) advertisement );
			miaps.setPropertyValue(id, value);
			return;
		}
		super.setPropertyValue(id, value);	
	}
}