package net.osgi.jxse.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.utils.StringStyler;

public interface IPeerGroupProvider {

	public enum PeerGroupDirectives implements IJxseDirectives{
		PEERGROUP;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}
		
		public static boolean isValidIdString( String id ){
			for( PeerGroupDirectives dir: values() ){
				if( dir.name().equals(id ))
					return true;
			}
			return false;
		}
	}
	
	/**
	 * Get the name of the peergroup provider
	 * @return
	 */
	public String getPeerGroupProviderName();
	
	/**
	 * This supportive interface allows various objects to provide a peer goup.
	 * The interface does not define WHEN the peergroup is available.
	 * @return
	 */
	public PeerGroup getPeerGroup();
}
