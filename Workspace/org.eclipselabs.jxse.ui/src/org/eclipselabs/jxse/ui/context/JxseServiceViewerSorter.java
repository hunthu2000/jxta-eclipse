package org.eclipselabs.jxse.ui.context;

import java.text.Collator;

import net.osgi.jxse.service.comparator.JxtaServiceComparator;

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
		JxtaServiceComparator<Object> comparator = new JxtaServiceComparator<Object>();
		return comparator.compare( e1, e2);
	}

	@Override
	public boolean isSorterProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return super.isSorterProperty(element, property);
	}
	
	

}
