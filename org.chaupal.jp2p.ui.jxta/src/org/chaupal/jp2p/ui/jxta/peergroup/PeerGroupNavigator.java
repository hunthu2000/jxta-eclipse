package org.chaupal.jp2p.ui.jxta.peergroup;

import net.jp2p.container.IJp2pContainer;
import net.jp2p.container.utils.SimpleNode;
import net.jp2p.jxta.peergroup.PeerGroupFactory;
import net.jxta.peergroup.PeerGroup;
import net.osgi.jp2p.chaupal.IServiceChangedListener;
import net.osgi.jp2p.chaupal.ServiceChangedEvent;
import net.osgi.jp2p.chaupal.ServiceEventDispatcher;
import net.osgi.jp2p.chaupal.utils.Utils;

import org.chaupal.jp2p.ui.container.Jp2pContainerNavigator;
import org.chaupal.jp2p.ui.jxta.osgi.service.PeerGroupPetitioner;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipselabs.osgi.ds.broker.service.IParlezListener;
import org.eclipselabs.osgi.ds.broker.service.ParlezEvent;

public class PeerGroupNavigator extends CommonNavigator{

	public static final String PATH_ID = "org.eclipselabs.jxse.ui.context.peergroups";
	
	private CommonViewer viewer;
	private PeerGroup peergroup;
	private PeerGroupPetitioner petitioner;
	private ServiceEventDispatcher dispatcher;
	private PeerGroupNavigator navigator;

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
		navigator = this;
		dispatcher = ServiceEventDispatcher.getInstance();
		dispatcher.addServiceChangeListener( new IServiceChangedListener(){

			@Override
			public void notifyServiceChanged(ServiceChangedEvent event) {
				if( ServiceChange.REFRESH.equals( event.getChange()))
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
        petitioner = PeerGroupPetitioner.getInstance();
		petitioner.addParlezListener( new IParlezListener(){

			@Override
			public void notifyChange(ParlezEvent<?> event) {
				navigator.refresh();
			}
			
		});
		petitioner.petition("containers");
		return petitioner.createPeerGroupTree();
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
		if(!( element instanceof IJp2pContainer<?>))
			return;
		IJp2pContainer<?> container = (net.jp2p.container.IJp2pContainer<Object>)element;
		SimpleNode<PeerGroup, PeerGroup> node = PeerGroupFactory.createPeerGroupTree( container );
		if( node != null ){
			this.peergroup = node.getData();
			viewer.setInput( node );
		}
		setContentDescription( Utils.getLabel( container ));
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