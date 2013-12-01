package org.eclipselabs.jxse.ui.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.utils.StringStyler;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipselabs.jxse.ui.provider.DecoratorLabelProvider;

public class PeerGroupPropertySource extends AbstractJxsePropertySource<PeerGroup, PeerGroupPropertySource.PeerGroupProperties> {

	public enum PeerGroupProperties{
		NAME,
		ID,
		PEER_ID,
		PEER_NAME,
		STORE_HOME;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
	}

	public PeerGroupPropertySource( PeerGroup peergroup ) {
		this( peergroup, new Properties() );
	}

	public PeerGroupPropertySource( IJxseComponent<PeerGroup, PeerGroupPropertySource.PeerGroupProperties> component ) {
		super( component );
	}

	public PeerGroupPropertySource( PeerGroup manager, Properties defaults ) {
		super( manager, defaults );
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Collection<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		descriptors.addAll( Arrays.asList( super.getPropertyDescriptors( PeerGroupProperties.values() )));
		if( super.getPropertyDescriptors() != null )
			descriptors.addAll( Arrays.asList( super.getPropertyDescriptors()));
		for( IPropertyDescriptor descriptor: descriptors ){
			PropertyDescriptor desc = (PropertyDescriptor)descriptor;
			desc.setLabelProvider( new DecoratorLabelProvider( false ));
		}
		return descriptors.toArray( new IPropertyDescriptor[ descriptors.size()]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		PeerGroup peergroup = super.getModule();
		if(!( id instanceof PeerGroupProperties ))
			return super.getPropertyValue(id);
		PeerGroupProperties property  = ( PeerGroupProperties )id;
		switch( property ){
		case ID:
			return peergroup.getPeerGroupID();
		case NAME:
			return peergroup.getPeerGroupName();
		case PEER_ID:
			return peergroup.getPeerID();
		case PEER_NAME:
			return peergroup.getPeerName();
		case STORE_HOME:
			return peergroup.getStoreHome();
		}
		return super.getPropertyValue(id);
	}

	/**
	 * Returns true if the given property can be modified
	 * @param id
	 * @return
	 */
	@Override
	public boolean isEditable( Object id ){
		return false;
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		super.setPropertyValue(id, value);
	}
}