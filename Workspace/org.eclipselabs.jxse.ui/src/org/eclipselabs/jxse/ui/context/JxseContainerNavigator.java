package org.eclipselabs.jxse.ui.context;

import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.service.IServiceChangedListener;
import net.osgi.jxse.service.ServiceChangedEvent;
import net.osgi.jxse.service.ServiceEventDispatcher;
import net.osgi.jxse.service.utils.Utils;

import org.eclipselabs.jxse.ui.osgi.service.JxseServiceContainerPetitioner;
import org.eclipselabs.osgi.ds.broker.service.IParlezListener;
import org.eclipselabs.osgi.ds.broker.service.ParlezEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

public class JxseContainerNavigator extends CommonNavigator{

	public static final String PATH_ID = "org.eclipselabs.jxse.ui.context.view";
	
	private CommonViewer viewer;
	
	private JxseServiceContainerPetitioner petitioner;
	private ServiceEventDispatcher dispatcher;
	private JxseContainerNavigator navigator;

	private ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			// we deal with only our own selections
			if (!( sourcepart instanceof JxseContainerNavigator ))
				return;
			showSelection( sourcepart, selection);
		}
	};

	public JxseContainerNavigator() {
		super();
		navigator = this;
		dispatcher = ServiceEventDispatcher.getInstance();
		dispatcher.addServiceChangeListener( new IServiceChangedListener(){

			@Override
			public void notifyServiceChanged(ServiceChangedEvent event) {
				navigator.refresh();
			}
			
		});
	}

	/**
	 * Declare a new root (standard this is the workspace).
	 * @see http://www.techjava.de/topics/2009/04/eclipse-common-navigator-framework/
	 */
	@Override
	protected Object getInitialInput() {
        petitioner = JxseServiceContainerPetitioner.getInstance();
		petitioner.addParlezListener( new IParlezListener(){

			@Override
			public void notifyChange(ParlezEvent<?> event) {
				navigator.refresh();
			}
			
		});
		petitioner.petition("peergroups");
		return petitioner;
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
		this.viewer.setSorter( new JxseServiceViewerSorter() );
		return viewer;
	}

	/**
	 * Shows the given selection in this view.
	 */
	@SuppressWarnings("unchecked")
	void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		if(!( element instanceof IJxseComponent<?>))
			return;
		IJxseComponent<?> component = (net.osgi.jxse.component.IJxseComponent<Object>)element;
		setContentDescription( Utils.getLabel(component));
	}
	
	protected void refresh(){
		Display.getDefault().asyncExec(new Runnable() {
            @Override
			public void run() {
            	viewer.refresh();
            }
         });			
	}
}