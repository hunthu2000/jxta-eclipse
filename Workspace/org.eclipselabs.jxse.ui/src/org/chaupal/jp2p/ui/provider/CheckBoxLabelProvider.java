package org.chaupal.jp2p.ui.provider;


import org.chaupal.jp2p.ui.image.LabelProviderImages;
import org.chaupal.jp2p.ui.image.LabelProviderImages.Images;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class CheckBoxLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element)
	{
		LabelProviderImages images = LabelProviderImages.getInstance();
		return Boolean.TRUE.equals(element) ? images.getImage( Images.CHECKED) : images.getImage( Images.UNCHECKED);
	}

	@Override
	public String getText(Object element)
	{
		return null;
	}

}
