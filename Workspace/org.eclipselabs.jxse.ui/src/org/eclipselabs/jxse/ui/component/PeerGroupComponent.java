package org.eclipselabs.jxse.ui.component;

import java.util.Map;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.JxseComponent;

public class PeerGroupComponent extends JxseComponent<PeerGroup> {

	public PeerGroupComponent(IJxseComponent<?> parent,
			PeerGroup component) {
		super(parent, component);
	}

	public PeerGroupComponent(PeerGroup component) {
		super(component);
	}

	@Override
	protected void fillProperties(Map<Object, Object> props) {
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
