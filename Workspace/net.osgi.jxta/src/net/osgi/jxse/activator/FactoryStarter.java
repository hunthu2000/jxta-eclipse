package net.osgi.jxse.activator;

import java.util.Iterator;
import java.util.concurrent.Callable;

import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.factory.FactoryNode;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Contexts;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class FactoryStarter<V extends Object> implements Callable<V> {

	private FactoryNode<V> node;
	
	public FactoryStarter( FactoryNode<V> node ) {
		this.node = node;
	}

	/**
	 * Parse the directives for this factory
	 * @param node
	 */
	private final void parseDirectives( FactoryNode<?> node ){
		IJxsePropertySource<IJxseProperties> propertySource = node.getData().getPropertySource();
		Iterator<IJxseDirectives> iterator = propertySource.directiveIterator();
		IJxseDirectives directive;
		while( iterator.hasNext()) {
			directive = iterator.next();
			this.onParseDirectivePriorToCreation( node, directive, ( String )propertySource.getDirective( directive ));
		}
	}

	protected void onParseDirectivePriorToCreation( FactoryNode<?> node, IJxseDirectives directive, String value) {
		if( node.getParent() == null )
			return;
		IComponentFactory<?> parentFactory = (IComponentFactory<?>) node.getParent().getData();
		if(!(directive instanceof Directives ))
			return;
		Directives dir = ( Directives )directive;
		switch( dir ){
			case ACTIVATE_PARENT:
				boolean ap = Boolean.parseBoolean( value );
				if( !ap || !parentFactory.isCompleted())
					break;
				Object pc = parentFactory.getComponent();
				if(!( pc instanceof IActivator ))
					return;
				IActivator activator = ( IActivator )pc;
				activator.start();
				break;
			case CREATE_PARENT:
				boolean cp = Boolean.parseBoolean( value );
				if( cp && !parentFactory.isCompleted())
					parentFactory.createComponent();
				break;
			default:
				break;
		}
	}

	/**
	 * Do nothing
	 */
	protected void onParseDirectiveAfterCreation( ComponentNode<IComponentFactory<?>> node, IJxseDirectives directive, String value) {
		if( node == null )
			return;
		IComponentFactory<?> factory = node.getData();
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
				Object pc = factory.getComponent();
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
		IComponentFactory<V> factory = node.getData();
		V module = null;
		if(factory.isCompleted() )
			return factory.getComponent();
		
		this.parseDirectives( node );
		try{
			do{ 
				module = factory.createComponent();
			}while( module == null );
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return module;
	}

}
