package org.chaupal.jp2p.ui.provider;


import org.chaupal.jp2p.ui.image.LabelProviderImages;
import org.chaupal.jp2p.ui.image.LabelProviderImages.Images;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

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
