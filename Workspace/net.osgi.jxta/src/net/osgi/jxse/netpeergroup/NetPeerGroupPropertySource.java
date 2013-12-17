package net.osgi.jxse.netpeergroup;

import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class NetPeerGroupPropertySource extends AbstractJxseWritePropertySource<IJxseProperties> {


	public NetPeerGroupPropertySource( IJxsePropertySource<?> parent) {
		super( Components.NET_PEERGROUP_SERVICE.toString(), parent);
	}

	@Override
	public IJxseProperties getIdFromString(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validate(IJxseProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}