package net.jp2p.container.context;

import java.util.ArrayList;
import java.util.Collection;

import net.jp2p.container.utils.Utils;

public class ContextLoader {

	private Collection<IJp2pContext<?>> contexts;
	
	public ContextLoader() {
		contexts = new ArrayList<IJp2pContext<?>>();
	}

	public void addContext( IJp2pContext<?> context ){
		this.contexts.add( context );
	}

	public void removeContext( IJp2pContext<?> context ){
		this.contexts.remove( context );
	}

	/**
	 * Get the context for the given name
	 * @param contextName
	 * @return
	 */
	public IJp2pContext<?> getContext( String contextName ){
		for( IJp2pContext<?> context: this.contexts ){
			if( Utils.isNull( contextName ))
				continue;
			if( context.getName().toLowerCase().equals( contextName.toLowerCase() ))
				return context;
		}
		return new Jp2pContext();
	}

	/**
	 * Get the context for the given componentname
	 * @param contextName
	 * @return
	 */
	public IJp2pContext<?> getContextForComponent( String contextName, String componentName ){
		if( Utils.isNull( componentName ))
			return new Jp2pContext();
		
		for( IJp2pContext<?> context: this.contexts ){
			if(context.isValidComponentName(contextName, componentName))
				return context;
		}
		return new Jp2pContext();
	}
}
