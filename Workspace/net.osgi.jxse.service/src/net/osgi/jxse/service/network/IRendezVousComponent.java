package net.osgi.jxse.service.network;

import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.utils.StringStyler;

public interface IRendezVousComponent {

	public enum RendezVousServiceProperties  implements IJxseProperties{
		STATUS,
		AUTO_START,
		IS_RENDEZVOUS,
		IS_CONNECTED_TO_RENDEZVOUS,
		RENDEZVOUS_STATUS;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
	}


	public abstract Object getProperty(RendezVousServiceProperties key);

	public abstract void putProperty(RendezVousServiceProperties key,
			Object value);

}