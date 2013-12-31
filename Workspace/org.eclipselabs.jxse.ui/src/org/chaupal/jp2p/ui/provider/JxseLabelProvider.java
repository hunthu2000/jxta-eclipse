package org.chaupal.jp2p.ui.provider;

import net.osgi.jp2p.component.AbstractJp2pService;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.service.discovery.ChaupalDiscoveryService;
import net.osgi.jp2p.service.utils.Utils;

import org.chaupal.jp2p.ui.image.StatusImages;
import org.chaupal.jp2p.ui.image.StatusImages.Images;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class JxseLabelProvider extends LabelProvider{

	@SuppressWarnings("unchecked")
	@Override
	public Image getImage(Object element) {
		if(!( element instanceof IJp2pComponent<?> ))
			return super.getImage(element);
		IJp2pComponent<?> component = (IJp2pComponent<?> )element;
		StatusImages images = new StatusImages();
		if( component instanceof AbstractJp2pService ){
			AbstractJp2pService<?> service = (AbstractJp2pService<Object> )component;
			return images.getImage( service.getStatus() );
		}
		return images.getImage( Images.COMPONENT );
	}

	@Override
	public String getText(Object element) {
		if(!( element instanceof IJp2pComponent<?>))
			return super.getText(element);
		IJp2pComponent<?> component = (IJp2pComponent<?> )element;
		String text = Utils.getLabel(component); 
		if( component instanceof ChaupalDiscoveryService )
		{
			ChaupalDiscoveryService service = ( ChaupalDiscoveryService )component;
			if( service.getSize() > 0 )
				text += "(" + service.getSize() + ")";
		}
		return text;
	}	
}