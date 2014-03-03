package org.chaupal.jp2p.ui.container;

import java.text.Collator;

import net.jp2p.chaupal.comparator.Jp2pServiceComparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class Jp2pServiceViewerSorter extends ViewerSorter {

	public Jp2pServiceViewerSorter() {
	}

	public Jp2pServiceViewerSorter(Collator collator) {
		super(collator);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Jp2pServiceComparator<Object> comparator = new Jp2pServiceComparator<Object>();
		return comparator.compare( e1, e2);
	}

	@Override
	public boolean isSorterProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return super.isSorterProperty(element, property);
	}
	
	

}
