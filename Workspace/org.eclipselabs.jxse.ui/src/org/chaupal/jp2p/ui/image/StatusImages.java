package org.chaupal.jp2p.ui.image;

import net.osgi.jp2p.activator.IActivator.Status;
import net.osgi.jp2p.utils.StringStyler;

import org.chaupal.jp2p.ui.Activator;
import org.eclipse.swt.graphics.Image;

public class StatusImages extends AbstractImages{

	public static final String S_ICON_PATH = "/resources/";

	public static final String S_IDLE_ICON  = "idle.png";
	public static final String S_AVAILABLE_ICON  = "available.png";
	public static final String S_INITIALISING_ICON  = "initialising.png";
	public static final String S_INITIALISED_ICON  = "initialised.png";
	public static final String S_ACTIVATING_ICON = "activating.png";
	public static final String S_ACTIVE_ICON = "active.png";
	public static final String S_PAUSED_ICON = "player_pause.png";
	public static final String S_STOPPING_ICON = "stop.png";
	public static final String S_FINALISING_ICON = "shuttingdown.png";
	public static final String S_FINALISED_ICON = "shutdown.png";

	public static final String S_COMPONENT_ICON = "component.png";
	public static final String S_WORLD_ICON = "world.png";

	public enum Images{
		COMPONENT,
		WORLD;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	private static StatusImages images = new StatusImages();
	
	public StatusImages() {
		super( S_ICON_PATH, Activator.PLUGIN_ID );
	}

	/**
	 * Get an instance of this map
	 * @return
	 */
	public static StatusImages getInstance(){
		return images;
	}
	
	@Override
	public void initialise(){
		setImage( S_IDLE_ICON );
		setImage( S_AVAILABLE_ICON );
		setImage( S_INITIALISING_ICON );
		setImage( S_INITIALISED_ICON );
		setImage( S_ACTIVATING_ICON );
		setImage( S_ACTIVE_ICON );
		setImage( S_PAUSED_ICON );
		setImage( S_STOPPING_ICON );
		setImage( S_FINALISING_ICON );
		setImage( S_FINALISED_ICON );
		setImage( S_COMPONENT_ICON );
		setImage( S_WORLD_ICON );
	}

	/**
	 * Get the image
	 * @param desc
	 * @return
	 */
	public Image getImage( Status desc ){
		switch( desc ){
		case IDLE:
			return getImageFromName( S_IDLE_ICON );
		case INITIALISING:
			return getImageFromName( S_INITIALISING_ICON );
		case INITIALISED:
			return getImageFromName( S_INITIALISED_ICON );
		case ACTIVATING:
			return getImageFromName( S_ACTIVATING_ICON );
		case ACTIVE:
			return getImageFromName( S_ACTIVE_ICON );
		case PAUSED:
			return getImageFromName( S_PAUSED_ICON );
		case AVAILABLE:
			return getImageFromName( S_AVAILABLE_ICON );
		case SHUTTING_DOWN:
			return getImageFromName( S_STOPPING_ICON );
		case FINALISING:
			return getImageFromName( S_FINALISING_ICON );
		case FINALISED:
			return getImageFromName( S_FINALISED_ICON );
		default:
			return getImageFromName( S_IDLE_ICON );				
		}
	}

	/**
	 * Get the image
	 * @param desc
	 * @return
	 */
	public Image getImage( Images desc ){
		switch( desc ){
		case COMPONENT:
			return getImageFromName( S_COMPONENT_ICON );
		case WORLD:
			return getImageFromName( S_WORLD_ICON );
		default:
			return getImageFromName( S_COMPONENT_ICON );				
		}
	}

}