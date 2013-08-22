package org.eclipselabs.jxse.ui.view.advertisements;

import net.jxta.document.Advertisement;
import net.osgi.jxse.component.IJxseComponent;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class JxseAdvertisementLabelProvider extends LabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		//Images images = Images.getInstance();
		if( element instanceof IJxseComponent<?>){
			//IJxtaServiceComponent<?> component = ( IJxtaServiceComponent<?> )element;
			return super.getImage(element);
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if( element instanceof AbstractTreeDecorator ){
			AbstractTreeDecorator<?,?,?> decorator = (AbstractTreeDecorator<?,?,?>)element;
			return decorator.getLabel();
		}
		if( element instanceof Advertisement ){
			Advertisement ad = ( Advertisement )element;
			return ad.getClass().getSimpleName() + " (" + ad.getAdvType() + "): " + ad.getID();
		}
		return super.getText(element);
	}	
}