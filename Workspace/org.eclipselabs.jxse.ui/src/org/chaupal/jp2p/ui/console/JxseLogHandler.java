package org.chaupal.jp2p.ui.console;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import net.osgi.jp2p.log.JxseLevel;

public class JxseLogHandler extends Handler {

	public JxseLogHandler() {
		this.setLevel( JxseLevel.getJxtaLevel() );
		super.setFormatter( new Formatter() {

		    private final SimpleDateFormat dateFormat =
		            new SimpleDateFormat("'['HH:mm:ss.SSS']'");

		    @Override
			public String format(LogRecord record) {
		    	Object[] parameters = record.getParameters();
		    	String identifier = (String) ((( parameters != null ) && ( parameters.length > 0 ) &&( parameters[0] instanceof String))? parameters[0]: null);  
		    	Calendar calendar = Calendar.getInstance();
		    	String logMessage = "JXTA ";
		    	if( identifier != null )
		    		logMessage += identifier;
		    	logMessage += " " + dateFormat.format( calendar.getTime() ) + ": " + record.getMessage() + "\n";
		    	return logMessage;
		    }
		});
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		if (!isLoggable(record))
			return;
		if( record.getLevel().intValue() > JxseLevel.getJxtaLevel().intValue() )
			return;
		// Output the formatted data to the file
		JxseConsole console = JxseConsole.getInstance();
		if( console == null )
			return;
     	console.println(getFormatter().format(record));
	}

}