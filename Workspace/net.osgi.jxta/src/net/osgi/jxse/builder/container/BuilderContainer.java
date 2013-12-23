package net.osgi.jxse.builder.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;

public class BuilderContainer implements ICompositeBuilderListener<Object>{

	private List<IJxseModule<Object>> modules;
	private Collection<IBuilderContainerListener<Object>> listeners;
	
	
	public BuilderContainer() {
		modules = new ArrayList<IJxseModule<Object>>();
		this.listeners = new ArrayList<IBuilderContainerListener<Object>>();
	}

	public boolean addListener(IBuilderContainerListener<Object> listener ){
		return this.listeners.add( listener );
	}

	public boolean removeListener(IBuilderContainerListener<?> listener ){
		return this.listeners.remove( listener );
	}
	
	public boolean addModule( IJxseModule<Object> module ){
		boolean retval = this.modules.add( module );
		Collections.sort(this.modules, new ModuleComparator());
		for( IBuilderContainerListener<Object> listener: this.listeners ){
			listener.notifyAdded( new BuilderContainerEvent<Object>( this, module ));
		}
		return retval;
	}

	public boolean removeModule( IJxseModule<Object> module ){
		boolean retval = this.modules.remove( module );
		for( IBuilderContainerListener<Object> listener: this.listeners ){
			listener.notifyAdded( new BuilderContainerEvent<Object>( this, module ));
		}
		return retval;
	}
	
	@SuppressWarnings("unchecked")
	public IJxseModule<Object>[] getChildren(){
		return this.modules.toArray( new IJxseModule[0] );
	}
	
	public IJxseModule<?> getModule( String name ){
		for( IJxseModule<?> module: modules ){
			if( module.getComponentName().equals(name ))
					return module;
		}
		return null;	
	}

	/**
	 * Returns true if all the modules have been completed
	 * @return
	 */
	public boolean isCompleted(){
		for( IJxseModule<?> module: modules ){
			if( !module.isCompleted())
				return false;
		}
		return true;
	}
	
	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		for( IJxseModule<Object> module: this.modules )
			if( !module.equals( event.getModule()))
				module.notifyCreated(event);
		
	}	
}

/**
 * This comparator comparse the weights of the modules
 * @author Kees
 *
 */
class ModuleComparator implements Comparator<IJxseModule<Object>>{

	@Override
	public int compare(IJxseModule<Object> arg0, IJxseModule<Object> arg1) {
		return arg0.getWeight() - arg1.getWeight();
	}
}