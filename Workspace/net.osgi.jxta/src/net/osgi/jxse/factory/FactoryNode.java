package net.osgi.jxse.factory;

import net.osgi.jxse.builder.ComponentNode;
import net.osgi.jxse.utils.Utils;

public class FactoryNode<T extends Object> extends
		ComponentNode<IComponentFactory<T>> {

	
	public FactoryNode() {
		super();
	}

	public FactoryNode(IComponentFactory<T> data) {
		super(data);
	}

	@Override
	public int compareTo(ComponentNode<IComponentFactory<T>> arg0) {
		if( arg0 == null )
			return 1;
		IComponentFactory<T> data = this.getData();
		if(( data == null ) && ( arg0.getData() == null ))
				return 0;
		if(( data != null ) && ( arg0.getData() == null ))
				return 1;
		if(( data == null ) && ( arg0.getData() != null ))
				return -1;

		return data.getPropertySource().getComponentName().compareTo( 
				arg0.getData().getPropertySource().getComponentName());
	}
	
	@Override
	public String toString() {
		return S_NODE + this.getData().getPropertySource().getComponentName().toString();
	}

	/**
	 * Get the child with the given name
	 * @param node
	 * @param componentName
	 * @return
	 */
	public static FactoryNode<?> getChild( FactoryNode<?> node, String componentName ){
		if( Utils.isNull( componentName ))
			return null;
		for( ComponentNode<?> child: node.getChildren()){
			FactoryNode<?> fn = (FactoryNode<?>) child;
			if( fn.getData().getPropertySource().getComponentName().equals( componentName ))
				return fn;
		}
		return null;
	}
}
