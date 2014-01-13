package org.chaupal.jp2p.ui.platform;

import net.jxta.platform.NetworkConfigurator;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.jxta.network.configurator.NetworkConfigurationFactory;

import org.chaupal.jp2p.ui.monitor.ConnectivityViewPart;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;

public class PlatformConfigViewer extends ViewPart {

	public static final String ID = "org.chaupal.jp2p.ui.platform.PlatformConfigViewer"; //$NON-NLS-1$

	private StyledText styledText;
	
	//The last selected network configurator;
	private NetworkConfigurator configurator;
	
	private ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			// we ignore our own selections
			if ( sourcepart instanceof ConnectivityViewPart )
				return;
			showSelection( sourcepart, selection);
		}
	};

	public PlatformConfigViewer() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		styledText = new StyledText(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.WRAP );

		createActions();
		initializeToolBar();
		initializeMenu();

		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
	}

	/**
	 * Shows the given selection in this view.
	 */
	void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
		if(!( selection instanceof TreeSelection))
			return;
		
		TreeSelection ss = (TreeSelection) selection;
		Object element = ss.getFirstElement();
		if(!( element instanceof IJp2pComponent ))
			return;
		IJp2pComponent<?> component = (IJp2pComponent<?>) element;
		configurator = NetworkConfigurationFactory.findNetworkConfigurator(component);
		this.setFocus();
	}
	
	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	@SuppressWarnings("unused")
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	@SuppressWarnings("unused")
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		if( configurator != null ){
			this.styledText.setText( configurator.getPlatformConfig().toString() );
		}else
			this.styledText.setText("");
	}
}
