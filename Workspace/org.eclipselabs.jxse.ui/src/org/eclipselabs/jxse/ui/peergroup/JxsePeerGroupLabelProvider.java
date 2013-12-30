package org.eclipselabs.jxse.ui.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.context.Swarm;
import net.osgi.jxse.service.utils.Utils;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.jxse.ui.image.StatusImages;
import org.eclipselabs.jxse.ui.image.StatusImages.Images;

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
		if( element instanceof IJxseComponent<?>){
			IJxseComponent<?> component = (IJxseComponent<?> )element;
			return Utils.getLabel(component); 
		}
		if( element instanceof PeerGroup ){
			PeerGroup peergroup = (PeerGroup )element;
			return peergroup.getPeerGroupName(); 
		}
		return super.getText(element);
	}
}	