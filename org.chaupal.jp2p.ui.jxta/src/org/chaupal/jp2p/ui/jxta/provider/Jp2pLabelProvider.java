package org.chaupal.jp2p.ui.jxta.provider;

import net.jp2p.container.activator.IActivator.Status;
import net.jp2p.container.component.AbstractJp2pService;
import net.jp2p.container.component.IJp2pComponent;
import net.osgi.jp2p.chaupal.utils.Utils;

import org.chaupal.jp2p.ui.jxta.image.StatusImages;
import org.chaupal.jp2p.ui.jxta.image.StatusImages.Images;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class Jp2pLabelProvider extends LabelProvider{

	@SuppressWarnings("unchecked")
	@Override
	public Image getImage(Object element) {
		if(!( element instanceof IJp2pComponent<?> ))
			return super.getImage(element);
		IJp2pComponent<?> component = (IJp2pComponent<?> )element;
		StatusImages images = new StatusImages();
		if( component instanceof AbstractJp2pService ){
			AbstractJp2pService<?> service = (AbstractJp2pService<Object> )component;
			if( service.getModule() == null )
				return images.getImage( Status.DISABLED );
			return images.getImage( service.getStatus() );
		}
		return images.getImage( Images.COMPONENT );
	}

	@Override
	public String getText(Object element) {
		if(!( element instanceof IJp2pComponent<?>))
			return super.getText(element);
		IJp2pComponent<?> component = (IJp2pComponent<?> )element;
		return Utils.getLabel(component); 
	}	
}