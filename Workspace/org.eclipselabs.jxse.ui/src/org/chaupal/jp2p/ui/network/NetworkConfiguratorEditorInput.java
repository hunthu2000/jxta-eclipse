/**
 * 
 */
package org.chaupal.jp2p.ui.network;

import net.osgi.jp2p.network.configurator.NetworkConfigurationPropertySource;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author roel
 *
 */
public class NetworkConfiguratorEditorInput implements IEditorInput {
	private NetworkConfigurationPropertySource source;

	public NetworkConfiguratorEditorInput(NetworkConfigurationPropertySource source) {
		this.source = source;
	}

	@Override
	public boolean exists() {
		return (this.source != null );
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return source.getIdentifier();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public NetworkConfigurationPropertySource getSource() {
		return source;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof NetworkConfiguratorEditorInput) {
			NetworkConfiguratorEditorInput b = (NetworkConfiguratorEditorInput) arg0;
			return b.equals(b);
		}
		return false;
	}
}
