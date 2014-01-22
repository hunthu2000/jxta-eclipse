package org.chaupal.jp2p.ui.view.advertisements;

import net.jp2p.container.component.IJp2pComponent;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class JxseAdvertisementContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(( parentElement == null ) || !( parentElement instanceof IJp2pComponent<?> ))
			return null;
		IJp2pComponent<?> decorator = (IJp2pComponent<?> )parentElement;
		return null;//decorator.getAdvertisements();
	}

	@Override
	public boolean hasChildren(Object element) {
		if(!( element instanceof IJp2pComponent ))
			return false;
		IJp2pComponent<?> decorator = (IJp2pComponent<?> )element;
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