package org.eclipselabs.jxse.ui.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.context.Swarm;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PeerGroupContentProvider implements ITreeContentProvider {

	private Swarm swarm;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(( parentElement == null ) || !( parentElement instanceof Swarm ))
			return null;
		this.swarm = (Swarm)parentElement;
		return swarm.getPeerGroups();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ( element instanceof Swarm );
	}

	@Override
	public void dispose() {
		this.swarm = null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren( inputElement );
	}

	@Override
	public Object getParent(Object element) {
		if(!(element instanceof PeerGroup )){
			return null;
		}
		return swarm;
	}
}