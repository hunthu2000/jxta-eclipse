package net.osgi.jxse.log;

import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.AbstractJxseWritePropertySource;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.utils.StringStyler;

public class LoggerPropertySource extends AbstractJxseWritePropertySource{

	public enum LoggerDirectives implements IJxseProperties{
		LOG_LEVEL;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	
	public LoggerPropertySource( IJxsePropertySource<IJxseProperties> parent) {
		super( Components.LOGGER_SERVICE.toString(), parent);
	}
}