package org.eclipselabs.jxse.ui.log;

import java.util.logging.LogRecord;

import org.eclipse.core.runtime.IStatus;
import org.eclipselabs.jxse.ui.Activator;

public class JxseLog {

	/**
	 * Log an info message
	 * @param message
	 */
	public static void logInfo( String message ){
		log( IStatus.INFO, IStatus.OK, message );
	}

	/**
	 * Log an error message
	 * @param message
	 */
	public static void logError( String message, Throwable exception ){
		log( IStatus.ERROR, IStatus.OK, message, exception );
	}

	/**
	 * Log an error message
	 * @param message
	 */
	public static void logError( Throwable exception ){
		logError( "An unexpected excpetion was thrown", exception );
		exception.printStackTrace();
	}

	/**
	 * Log an error message
	 * @param message
	 */
	public static void logJxta( LogRecord record ){
		log( IStatus.INFO, IStatus.OK, record.getMessage() );
	}

	public static void log( int severity, int code, String message ){
		
		Activator.getDefault().getLog().log( severity, message );
	}

	public static void log( int severity, int code, String message, Throwable exception ){
		
		Activator.getDefault().getLog().log( severity, message, exception);
	}
}
