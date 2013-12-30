package org.eclipselabs.jxse.ui.view.advertisements;

import net.osgi.jxse.component.IJxseComponent;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class JxseAdvertisementContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(( parentElement == null ) || !( parentElement instanceof IJxseComponent<?> ))
			return null;
		IJxseComponent<?> decorator = (IJxseComponent<?> )parentElement;
		return null;//decorator.getAdvertisements();
	}

	@Override
	public boolean hasChildren(Object element) {
		if(!( element instanceof IJxseComponent ))
			return false;
		IJxseComponent<?> decorator = (IJxseComponent<?> )element;
		return false;//decorator.hasAdvertisements();
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
		if(!(element instanceof AbstractTreeDecorator )){
			return null;
		}
		AbstractTreeDecorator<?,?,?> decorator = (AbstractTreeDecorator<?,?,?>)element;
		if( decorator.isRoot() )
			return null;
		return decorator.getParent();
	}
}