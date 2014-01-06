package org.chaupal.jp2p.ui.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jp2p.chaupal.utils.Utils;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.container.Swarm;

import org.chaupal.jp2p.ui.image.StatusImages;
import org.chaupal.jp2p.ui.image.StatusImages.Images;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class JxsePeerGroupLabelProvider extends LabelProvider{

	@Override
	public Image getImage(Object element) {
		StatusImages images = new StatusImages();
		if( element instanceof Swarm )
			return images.getImage( Images.COMPONENT );
		if( element instanceof PeerGroup )
			return images.getImage( Images.WORLD );
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if( element instanceof IJp2pComponent<?>){
			IJp2pComponent<?> component = (IJp2pComponent<?> )element;
			return Utils.getLabel(component); 
		}
		if( element instanceof PeerGroup ){
			PeerGroup peergroup = (PeerGroup )element;
			return peergroup.getPeerGroupName(); 
		}
		return super.getText(element);
	}
}	