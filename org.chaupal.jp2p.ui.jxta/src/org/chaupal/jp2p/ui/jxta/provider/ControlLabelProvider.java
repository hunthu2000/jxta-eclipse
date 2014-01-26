package org.chaupal.jp2p.ui.jxta.provider;


import org.chaupal.jp2p.ui.jxta.image.LabelProviderImages;
import org.chaupal.jp2p.ui.jxta.image.LabelProviderImages.Images;
import org.chaupal.jp2p.ui.jxta.property.descriptors.AbstractControlPropertyDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ControlLabelProvider extends LabelProvider {

	private AbstractControlPropertyDescriptor<?> descriptor;
	
	public ControlLabelProvider( AbstractControlPropertyDescriptor<?> descriptor) {
		super();
		this.descriptor = descriptor;
	}

	@Override
	public Image getImage(Object element)
	{
		LabelProviderImages images = LabelProviderImages.getInstance();
		if( this.descriptor == null )
			return null;
		if ( !this.descriptor.isEnabled() )
			return images.getImage( Images.NON_WRITABLE );
		return super.getImage(element);//Boolean.TRUE.equals(element) ? images.getImage( Images.CHECKED) : images.getImage( Images.UNCHECKED);
	}

	@Override
	public String getText(Object element)
	{
		return super.getText(element);
	}

}
