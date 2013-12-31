package org.chaupal.jp2p.ui.context;

import java.text.Collator;

import net.osgi.jp2p.service.comparator.Jp2pServiceComparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class JxseServiceViewerSorter extends ViewerSorter {

	public JxseServiceViewerSorter() {
	}

	public JxseServiceViewerSorter(Collator collator) {
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
