package net.osgi.jxse.log;

import java.util.logging.Logger;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory.Components;

public class LoggerModule extends AbstractJxseModule<LoggerComponent, LoggerPropertySource> {

	private Logger logger = Logger.getLogger(LoggerModule.class.getName());
	
	public LoggerModule(IJxseModule<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.LOGGER_SERVICE.toString();
	}

	@Override
	public LoggerPropertySource onCreatePropertySource() {
		LoggerPropertySource source = new LoggerPropertySource( super.getParent().getPropertySource());
		return source;
	}

	@Override
	public LoggerFactory onCreateFactory() {
		return new LoggerFactory( super.getPropertySource() );
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		String msg = event.getBuilderEvent().toString() + ": " + event.getModule().getComponentName();
		logger.log( JxseLevel.JXSELEVEL, msg );
		System.out.println(msg);
	}
}
