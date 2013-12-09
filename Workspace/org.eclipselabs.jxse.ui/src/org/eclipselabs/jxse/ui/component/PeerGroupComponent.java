package org.eclipselabs.jxse.ui.component;

import java.util.Map;

import org.eclipselabs.jxse.ui.property.PeerGroupPropertySource;
import org.eclipselabs.jxse.ui.property.PeerGroupPropertySource.PeerGroupProperties;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponent;

public class PeerGroupComponent extends JxseComponent<PeerGroup, PeerGroupPropertySource.PeerGroupProperties> {

	public PeerGroupComponent(IJxseComponent<?,?> parent,
			PeerGroup component) {
		super(parent, component);
	}

	public PeerGroupComponent(PeerGroup component) {
		super(component);
	}

	
	@Override
	protected void fillProperties(Map<PeerGroupProperties, Object> props) {
		PeerGroup pg = super.getModule();
		super.addAdvertisement( pg.getImplAdvertisement() );
		super.addAdvertisement( pg.getConfigAdvertisement());
		super.addAdvertisement( pg.getPeerAdvertisement() );
		super.addAdvertisement( pg.getPeerGroupAdvertisement() );
		try {
			super.addAdvertisement( pg.getAllPurposePeerGroupImplAdvertisement() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.fillProperties(props);
	}
}
