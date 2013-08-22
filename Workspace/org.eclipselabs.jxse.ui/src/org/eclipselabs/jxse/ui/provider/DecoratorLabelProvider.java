package org.eclipselabs.jxse.ui.provider;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.jxse.ui.image.LabelProviderImages;
import org.eclipselabs.jxse.ui.image.LabelProviderImages.Images;

public class DecoratorLabelProvider extends LabelProvider {

	private boolean enabled;
	
	public DecoratorLabelProvider( boolean enabled ) {
		super();
		this.enabled = enabled;
	}

	@Override
	public Image getImage(Object element)
	{
		LabelProviderImages images = LabelProviderImages.getInstance();
		if ( !this.enabled )
			return images.getImage( Images.ERROR );
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		return super.getText(element);
	}

}
