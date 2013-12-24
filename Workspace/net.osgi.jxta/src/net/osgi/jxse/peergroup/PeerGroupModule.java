package net.osgi.jxse.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;

public class PeerGroupModule extends AbstractJxseModule<PeerGroup, PeerGroupPropertySource> {

	private PeerGroup parentPeergroup;
	
	@Override
	public String getComponentName() {
		return Components.PEERGROUP_SERVICE.toString();
	}
	
	@Override
	protected PeerGroupPropertySource onCreatePropertySource() {
		return new PeerGroupPropertySource( super.getParent().getPropertySource());
	}

	@Override
	public IComponentFactory<PeerGroup> onCreateFactory() {
		return new PeerGroupFactory( super.getPropertySource(), parentPeergroup);
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
