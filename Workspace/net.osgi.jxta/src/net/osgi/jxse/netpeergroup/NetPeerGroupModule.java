package net.osgi.jxse.netpeergroup;

import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class NetPeerGroupModule implements IJxseModule<NetPeerGroupService> {

	private NetPeerGroupPropertySource source;
	
	@Override
	public String getComponentName() {
		return Components.PIPE_SERVICE.toString();
	}

	@Override
	public IJxsePropertySource<IJxseProperties> createPropertySource( IJxsePropertySource<?> parent ) {
		this.source = new NetPeerGroupPropertySource( parent );
		return source;
	}

	/**
	 * Get the property source that is used for the factor, or null if it wasn't created yet
	 * @return
	 */
	public IJxsePropertySource<IJxseProperties> getPropertySource(){
		return this.source;
	}

	@Override
	public void setProperty(IJxseProperties id, Object value) {
		this.source.setProperty(id, value);	
	}

	@Override
	public IComponentFactory<NetPeerGroupService, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new NetPeerGroupFactory( source );
	}
}
