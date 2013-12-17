package net.osgi.jxse.activator;

import java.util.Iterator;
import java.util.concurrent.Callable;

import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Contexts;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class FactoryStarter<V extends Object> implements Callable<V> {

	private ComponentNode<V,IJxseProperties> node;
	
	public FactoryStarter( ComponentNode<V,IJxseProperties> node ) {
		this.node = node;
	}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( ComponentNode<?,?> node ){
		IJxsePropertySource<?> propertySource = node.getFactory().getPropertySource();
		Iterator<IJxseDirectives> iterator = propertySource.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext()) {
			directive = iterator.next();
			this.onParseDirectivePriorToCreation( node, directive, ( String )propertySource.getDirective( directive ));
		}
	}

	protected void onParseDirectivePriorToCreation( ComponentNode<?,?> node, IJxseDirectives directive, String value) {
		if( node.getParent() == null )
			return;
		IComponentFactory<?,?> parentFactory = node.getParent().getFactory();
		if(!(directive instanceof Directives ))
			return;
		Directives dir = ( Directives )directive;
		switch( dir ){
			case ACTIVATE_PARENT:
				boolean ap = Boolean.parseBoolean( value );
				if( !ap || !parentFactory.isCompleted())
					break;
				Object pc = parentFactory.getModule();
				if(!( pc instanceof IActivator ))
					return;
				IActivator activator = ( IActivator )pc;
				activator.start();
				break;
			case CREATE_PARENT:
				boolean cp = Boolean.parseBoolean( value );
				if( cp && !parentFactory.isCompleted())
					parentFactory.createModule();
				break;
			default:
				break;
		}
	}

	/**
	 * Do nothing
	 */
	protected void onParseDirectiveAfterCreation( ComponentNode<?,?> node, IJxseDirectives directive, String value) {
		if( node == null )
			return;
		IComponentFactory<?,?> factory = node.getFactory();
		if(( !factory.isCompleted() ) || !(directive instanceof Directives ))
			return;
		Directives dir = ( Directives )directive;
		switch( dir ){
		case CONTEXT:
			if( Utils.isNull( value ))
				break;
			Contexts type = Contexts.valueOf( StringStyler.styleToEnum(value));
			switch( type ){
			case CHAUPAL:
				break;
			default:
				break;
			}
			
			break;
		case AUTO_START:
				boolean ap = Boolean.parseBoolean( value );
				if( !ap )
					break;
				Object pc = factory.getModule();
				if(!( pc instanceof IActivator ))
					return;
				IActivator activator = ( IActivator )pc;
				activator.start();
				break;
			default:
				break;
		}
	}
	@Override
	public V call() throws Exception {
		IComponentFactory<V,?> factory = node.getFactory();
		V module = null;
		if(factory.isCompleted() )
			return factory.getModule();
		
		this.parseDirectives( node );
		try{
			do{ 
				module = factory.createModule();
			}while( module == null );
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return module;
	}

}
