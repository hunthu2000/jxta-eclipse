package org.eclipselabs.jxse.ui.provider;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.jxse.ui.image.LabelProviderImages;
import org.eclipselabs.jxse.ui.image.LabelProviderImages.Images;

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
