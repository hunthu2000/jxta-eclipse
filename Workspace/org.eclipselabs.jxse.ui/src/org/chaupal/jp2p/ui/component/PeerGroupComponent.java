package org.chaupal.jp2p.ui.component;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.Jp2pComponent;

public class PeerGroupComponent extends Jp2pComponent<PeerGroup> {

	public PeerGroupComponent(IJp2pComponent<?> parent,
			PeerGroup component) {
		super(parent, component);
	}

	public PeerGroupComponent(PeerGroup component) {
		super(component);
	}
}