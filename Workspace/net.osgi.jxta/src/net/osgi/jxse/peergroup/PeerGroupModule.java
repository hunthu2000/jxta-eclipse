package net.osgi.jxse.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;

public class PeerGroupModule extends AbstractJxseModule<PeerGroup, PeerGroupPropertySource> {

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}
	
	@Override
	protected PeerGroupPropertySource onCreatePropertySource() {
		return new PeerGroupPropertySource( super.getParent());
	}

	@Override
	public IComponentFactory<PeerGroup, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new PeerGroupFactory( provider, super.getPropertySource() );
	}
}
