package net.jp2p.container.log;

import net.jp2p.container.context.Jp2pContext;
import net.jp2p.container.properties.AbstractJp2pWritePropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.utils.StringStyler;

public class LoggerPropertySource extends AbstractJp2pWritePropertySource{

	public enum LoggerDirectives implements IJp2pProperties{
		LOG_LEVEL;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	public LoggerPropertySource( IJp2pPropertySource<IJp2pProperties> parent) {
		super( Jp2pContext.Components.LOGGER_SERVICE.toString(), parent);
	}
}