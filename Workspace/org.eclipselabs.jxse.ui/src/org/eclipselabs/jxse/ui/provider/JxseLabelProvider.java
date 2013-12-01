package org.eclipselabs.jxse.ui.provider;

import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.service.core.AbstractJxseService;
import net.osgi.jxse.service.utils.Utils;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.jxse.ui.image.StatusImages;
import org.eclipselabs.jxse.ui.image.StatusImages.Images;

public class JxseLabelProvider extends LabelProvider{

	@SuppressWarnings("unchecked")
	@Override
	public Image getImage(Object element) {
		if(!( element instanceof IJxseComponent ))
			return super.getImage(element);
		IJxseComponent<?,?> component = (IJxseComponent<?,?> )element;
		StatusImages images = new StatusImages();
		if( component instanceof AbstractJxseService ){
			AbstractJxseService<?,?,?> service = (net.osgi.jxse.service.core.AbstractJxseService<Object,?,?> )component;
			return images.getImage( service.getStatus() );
		}
		return images.getImage( Images.COMPONENT );
	}

	@Override
	public String getText(Object element) {
		if(!( element instanceof IJxseComponent<?,?>))
			return super.getText(element);
		IJxseComponent<?,?> component = (IJxseComponent<?,?> )element;
		return Utils.getLabel(component);
	}	
}