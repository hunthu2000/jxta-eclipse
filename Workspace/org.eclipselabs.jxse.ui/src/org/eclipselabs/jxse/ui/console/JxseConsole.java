package org.eclipselabs.jxse.ui.console;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipselabs.jxse.ui.util.ColorUtils;
import org.eclipselabs.jxse.ui.util.ColorUtils.SupportedColors;

public class JxseConsole extends MessageConsole {

	public static final String S_CONSOLE_NAME = "Jxta Console";

	private PrintStream ps;
	private StreamData current;

	private static JxseConsole console;
	
	private String source;
	
	private Map<String, StreamData> coding;
	
	public JxseConsole() {
		this( S_CONSOLE_NAME );
		coding = new HashMap<String, StreamData>();
	}

	public JxseConsole(String name ) {
		super(name, null );
		console = this;
	}

	public static JxseConsole getInstance(){
		if( console == null ){
			console = new JxseConsole();
			console.init();
			console.activate();
		}
		return console;
	}
	
	public JxseConsole(String name, ImageDescriptor imageDescriptor,
			boolean autoLifecycle) {
		super(name, imageDescriptor, autoLifecycle);
	}

	public JxseConsole(String name, String consoleType,
			ImageDescriptor imageDescriptor, boolean autoLifecycle) {
		super(name, consoleType, imageDescriptor, autoLifecycle);
	}

	public JxseConsole(String name, String consoleType,
			ImageDescriptor imageDescriptor, String encoding,
			boolean autoLifecycle) {
		super(name, consoleType, imageDescriptor, encoding, autoLifecycle);
	}

	public String getSource() {
		return source;
	}

	public void setSource( String source ) {
		this.source = source;
	}

	@Override
	protected void init() {
		this.source = S_CONSOLE_NAME;
		this.current = getStream( this.source );
		super.init();
	}

	/**
	 * Get a new stream
	 * @param identifier
	 * @return
	 */
	protected StreamData getStream( String identifier ){
		StreamData data = this.coding.get( identifier );
		if( data != null ){
			if( identifier.equals( this.source ))
				return data;
			this.changeStream(data);
			return data;
		}
		data = new StreamData();
		data.stream = this.newMessageStream();
		data.stream.setActivateOnWrite(true);
		SupportedColors color = ( this.current == null )? SupportedColors.COLOR_BLACK: this.current.color; 
		data.color = ColorUtils.getNextColor( color );
		this.current = data;
		
		data.stream.setColor( ColorUtils.getSWTColor( Display.getDefault(), this.current.color ));
		coding.put( identifier, data );
		this.changeStream(data);
		return data;	
	}
	
	/**
	 * Change the stream by rerouting the sys.err and sys.out to a different print stream
	 * @param stream
	 */
	protected void changeStream( StreamData data ){
		if( ps != null ){
			ps.flush();
			ps.close();
		}
		ps = new PrintStream( data.stream);
		System.setOut( ps );
		System.setErr( ps );		
	}
	
	@Override
	protected void dispose() {
		Iterator<StreamData> iterator = this.coding.values().iterator();
		StreamData data;
		while( iterator.hasNext()){
			data = iterator.next();
			try {
				Color color = data.stream.getColor();
				data.stream.flush();
				data.stream.close();
				color.dispose();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.dispose();
	}

	public void print( String msg ){
		current.stream.print( msg );
	}

	public void println( String msg ){
		current.stream.println( msg );
	}

	public void println(){
		current.stream.println();
	}
}

class StreamData{
	MessageConsoleStream stream;
	SupportedColors color;
}
