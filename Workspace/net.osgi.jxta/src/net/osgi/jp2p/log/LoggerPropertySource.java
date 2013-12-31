package net.osgi.jp2p.log;

import net.osgi.jp2p.factory.IComponentFactory.Components;
import net.osgi.jp2p.properties.AbstractJp2pWritePropertySource;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.StringStyler;

public class LoggerPropertySource extends AbstractJp2pWritePropertySource{

	public enum LoggerDirectives implements IJp2pProperties{
		LOG_LEVEL;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	public LoggerPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( Components.LOGGER_SERVICE.toString(), parent);
	}
}