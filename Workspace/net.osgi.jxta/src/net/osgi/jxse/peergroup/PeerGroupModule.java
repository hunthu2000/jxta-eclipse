package net.osgi.jxse.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;

public class PeerGroupModule extends AbstractJxseModule<PeerGroup, PeerGroupPropertySource> {

	private  IPeerGroupProvider provider;

	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}
	
	@Override
	protected PeerGroupPropertySource onCreatePropertySource() {
		return new PeerGroupPropertySource( super.getParent().getPropertySource());
	}

	@Override
	public IComponentFactory<PeerGroup> onCreateFactory() {
		return new PeerGroupFactory( provider, super.getPropertySource() );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
