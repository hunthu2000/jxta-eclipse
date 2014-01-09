package org.chaupal.jp2p.ui.peergroup;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jp2p.chaupal.utils.Utils;
import net.osgi.jp2p.container.IJxseServiceContainer;
import net.osgi.jp2p.container.Swarm;

import org.chaupal.jp2p.ui.container.Jp2pContainerNavigator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

public class PeerGroupNavigator extends CommonNavigator{

	public static final String PATH_ID = "org.eclipselabs.jxse.ui.context.peergroups";
	
	private CommonViewer viewer;
	private Swarm<PeerGroup> swarm;
	
	private ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			// we deal with only our own selections
			if (!( sourcepart instanceof Jp2pContainerNavigator ))
				return;
			showSelection( sourcepart, selection);
		}
	};

	public PeerGroupNavigator() {
		super();
	}

	/**
	 * Declare a new root (standard this is the workspace).
	 * @see http://www.techjava.de/topics/2009/04/eclipse-common-navigator-framework/
	 */
	@Override
	protected Object getInitialInput() {
        if( swarm == null )
        	return null;
		return null;//swarm.getPeerGroups();
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
		return viewer;
	}

	/**
	 * Shows the given selection in this view.
	 */
	@SuppressWarnings("unchecked")
	void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		if(!( element instanceof IJxseServiceContainer<?>))
			return;
		IJxseServiceContainer<?> context = ( IJxseServiceContainer<Object>)element;
		viewer.setInput( context.getSwarm());
		setContentDescription( Utils.getLabel( context ));
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