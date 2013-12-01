package org.eclipselabs.jxse.ui.context;

import net.osgi.jxse.component.IJxseComponentNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class JxseServiceContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(( parentElement == null ) || !( parentElement instanceof IJxseComponentNode<?,?> ))
			return null;
		 IJxseComponentNode<?,?> decorator = (IJxseComponentNode<?,?>)parentElement;
		if( decorator.getChildren() == null )
			return null;
		//Collections.sort( decorator.getChildren(), new JxtaServiceComparator< IJxtaServiceNode<?>>());
		return decorator.getChildren().toArray();
	}

	@Override
	public boolean hasChildren(Object element) {
		if(!( element instanceof IJxseComponentNode ))
			return false;
		IJxseComponentNode<?,?> decorator = (IJxseComponentNode<?,?>)element;
		return decorator.hasChildren();
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren( inputElement );
	}

	@Override
	public Object getParent(Object element) {
		if(!(element instanceof IJxseComponentNode )){
			return null;
		}
		IJxseComponentNode<?,?> component = (IJxseComponentNode<?,?>)element;
		if( component.isRoot() )
			return null;
		return component.getParent();
	}
}