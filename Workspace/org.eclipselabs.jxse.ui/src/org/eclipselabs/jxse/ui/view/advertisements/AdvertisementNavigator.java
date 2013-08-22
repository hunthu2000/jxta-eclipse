package org.eclipselabs.jxse.ui.view.advertisements;

import net.osgi.jxse.component.IJxseComponent;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

public class AdvertisementNavigator extends CommonNavigator{

	public static final String PATH_ID = "org.condast.jxta.ide.view.jxta.advertisements";
	public static final String PART_NAME = "JXSE Advertisement Viewer";
	
	private CommonViewer viewer;
	
	private IJxseComponent<?> component;

	private ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			// we ignore our own selections
			if ( sourcepart instanceof AdvertisementNavigator )
				return;
			showSelection( sourcepart, selection);
		}
	};

	public AdvertisementNavigator() {
		super();
		this.setPartName( PART_NAME );
	}

	/**
	 * Declare a new root (standard this is the workspace).
	 * @see http://www.techjava.de/topics/2009/04/eclipse-common-navigator-framework/
	 */
	@Override
	protected Object getInitialInput() {
		return component;
	}

	@Override
	public void createPartControl(Composite aParent) {
		getSite().setSelectionProvider(this.getCommonViewer());
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
		super.createPartControl(aParent);
	}

	@Override
	protected CommonViewer createCommonViewer(Composite aParent) {
		this.viewer = super.createCommonViewer(aParent);
		//this.viewer.setComparator( new JxseServiceViewerSorter() );
		return viewer;
	}

	/**
	 * Shows the given selection in this view.
	 */
	void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
		if(!( selection instanceof IStructuredSelection))
			return;
		
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		
		//We check for service decorators coming from the service navigator
		if(!( element instanceof IJxseComponent<?>))
			return;
		IJxseComponent<?> component = (net.osgi.jxse.component.IJxseComponent<?> )element;
		viewer.setInput( component );
		viewer.refresh(true);
	}

	@Override
	public void dispose() {
		//this.discovery.removeParlezListener(this);
		super.dispose();
	}
	
	/**  
	 * Passing the focus request to the viewer's control. 
	 */
	  @Override
	public void setFocus() {
	    viewer.getControl().setFocus();
	  }
}