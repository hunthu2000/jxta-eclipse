package org.chaupal.jp2p.ui.image;

import net.jp2p.container.utils.StringStyler;

import org.chaupal.jp2p.ui.Activator;
import org.eclipse.swt.graphics.Image;

public class LabelProviderImages extends AbstractImages{

	public static final String S_ICON_PATH = "/resources/";

	public static final String S_CHECKED_ICON  = "checked.png";
	public static final String S_UNCHECKED_ICON  = "unchecked.png";
	public static final String S_QUESTION_MARK_ICON  = "question.png";
	public static final String S_ERROR_ICON  = "error.png";
	public static final String S_WRITABLE_ICON  = "writable.png";
	public static final String S_NON_WRITABLE_ICON  = "non-writable.png";
	
	public enum Images{
		CHECKED,
		UNCHECKED,
		QUESTION_MARK,
		WRITABLE,
		NON_WRITABLE,
		ERROR;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	private static LabelProviderImages images = new LabelProviderImages();
	
	public LabelProviderImages() {
		super( S_ICON_PATH, Activator.BUNDLE_ID );
	}

	/**
	 * Get an instance of this map
	 * @return
	 */
	public static LabelProviderImages getInstance(){
		return images;
	}
	
	@Override
	public void initialise(){
		setImage( S_CHECKED_ICON );
		setImage( S_UNCHECKED_ICON );
		setImage( S_QUESTION_MARK_ICON );
		setImage( S_ERROR_ICON );
		setImage( S_WRITABLE_ICON );
		setImage( S_NON_WRITABLE_ICON );
	}

	/**
	 * Get the image
	 * @param desc
	 * @return
	 */
	public Image getImage( Images desc ){
		switch( desc ){
		case CHECKED:
			return getImageFromName( S_CHECKED_ICON );
		case UNCHECKED:
			return getImageFromName( S_UNCHECKED_ICON );
		case ERROR:
			return this.getImageFromName( S_ERROR_ICON );
		case WRITABLE:
			return this.getImageFromName( S_WRITABLE_ICON );
		case NON_WRITABLE:
			return this.getImageFromName( S_NON_WRITABLE_ICON );
		default:
			return getImageFromName( S_QUESTION_MARK_ICON );				
		}
	}

}