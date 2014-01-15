package org.chaupal.jp2p.ui.rendezvous;

import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.rendezvous.RendezVousService;
import net.jxta.rendezvous.RendezvousEvent;
import net.jxta.rendezvous.RendezvousListener;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.log.Jp2pLevel;

import org.chaupal.jp2p.ui.log.Jp2pLog;
import org.chaupal.jp2p.ui.monitor.ConnectivityViewPart;
import org.chaupal.jp2p.ui.util.ColorUtils;
import org.chaupal.jp2p.ui.util.ColorUtils.SupportedColors;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.StyledText;

public class RendezvousServiceViewPart extends ViewPart{

	public static final String ID = "net.equinox.jxta.ui.monitor.ConnectivityViewPart"; //$NON-NLS-1$
	public static final String S_CONNECTIVITY_MONITOR = "Connectivity Monitor";
	private Button aliveRadioButton;
	private Button isRDVCheckBox;
	private Button isConnectedToRDVCheckBox;
	
	private TableViewer tableViewer;
	private TableViewer edgesTableViewer;

   private RdvEventMonitor rdvMonitor;
    
    private Future<?> theMonitorFuture = null;
	public static final ScheduledExecutorService theExecutor = Executors.newScheduledThreadPool(5);

	private Table table;
	private Table edgesTable;
	private Composite composite_1;
	private StyledText styledText;

	private RendezVousService rdvService;
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private boolean isrunning;

	private Runnable runner = new Runnable(){

		@Override
		public void run() {
			isrunning = true;
			if( rdvService != null )
	            refresh();
		}
	};

	private ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			// we ignore our own selections
			if ( sourcepart instanceof ConnectivityViewPart )
				return;
			showSelection( sourcepart, selection);
		}
	};

	public RendezvousServiceViewPart() {
		setPartName( S_CONNECTIVITY_MONITOR );
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
				// we ignore our own selections
				if ( sourcepart instanceof RendezvousServiceViewPart )
					return;
				showSelection( sourcepart, selection);
			}
		});
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
				
				Composite composite_2 = new Composite(sashForm, SWT.NONE);
				composite_2.setLayout(new GridLayout(2, false));
				
				aliveRadioButton = new Button(composite_2, SWT.RADIO);
				aliveRadioButton.setBounds(0, 0, 90, 16);
				aliveRadioButton.setText("Alive");
				
						isRDVCheckBox = new Button(composite_2, SWT.CHECK);
						isRDVCheckBox.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								IsRDVCheckBoxActionPerformed(e);			}
						});
						isRDVCheckBox.setText("is RDV");
						
								isConnectedToRDVCheckBox = new Button(composite_2, SWT.CHECK);
								isConnectedToRDVCheckBox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
								isConnectedToRDVCheckBox.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent e) {
										IsConnectedToRDVCheckBoxActionPerformed(e);
									}
								});
								isConnectedToRDVCheckBox.setText("is connected to RDV");
		
		composite_1 = new Composite( composite_2, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_4 = new Composite(composite_1, SWT.NONE);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));

		edgesTableViewer = new TableViewer(composite_4, SWT.BORDER | SWT.FULL_SELECTION);
		createColumn("Edges", edgesTableViewer);
		edgesTableViewer.setColumnProperties(new String[] {"Relays"});
		edgesTable = edgesTableViewer.getTable();
		edgesTable.setHeaderVisible(true);
		edgesTable.setLinesVisible(true);
		edgesTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		Composite composite_3 = new Composite(composite_1, SWT.NONE);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tableViewer = new TableViewer(composite_3, SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumn("Relays", tableViewer);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		styledText = new StyledText(sashForm, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		toolkit.adapt(styledText);
		toolkit.paintBordersFor(styledText);

		sashForm.setWeights(new int[] {3, 1});

		createActions();
		initializeToolBar();
		initializeMenu();
        theMonitorFuture = theExecutor.scheduleAtFixedRate( runner, 5, 1, TimeUnit.SECONDS);
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
	}

	private void createColumn( String name, TableViewer viewer ){
		TableViewerColumn col = createTableViewerColumn( viewer, name, 100, 0 );
		col.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    return super.getText(element);
		  }
		});		
	}

	private TableViewerColumn createTableViewerColumn( TableViewer viewer, String title, int bound, final int colNumber) {
	    final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,  SWT.NONE);
	    TableColumn column = viewerColumn.getColumn();
	    column.setText(title);
	    column.setWidth(bound);
	    column.setResizable(true);
	    column.setMoveable(true);
	    return viewerColumn;
	}
	
	public void setInput( ConfigMode mode, List<String> values ){
		switch( mode){
		case EDGE:
			edgesTableViewer.setInput( values );
		default: 
			tableViewer.setInput(values);
		}
	}
	
    // Resetting frame value
    public void resettingFrameValues() {        
        this.setPartName("Connectivity Monitor");
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
		if(!( element instanceof IJp2pComponent<?>))
			return;
		IJp2pComponent<?> component = (IJp2pComponent<?> )element;
		if(!( component.getModule() instanceof PeerGroup ))
			return;
		PeerGroup peergroup = ((PeerGroup) component.getModule());
		this.rdvService = peergroup.getRendezVousService();
         if( this.rdvMonitor != null ){
             this.rdvService.removeListener(this.rdvMonitor);        	 
         }
		 this.rdvMonitor = new RdvEventMonitor();
         this.rdvService.addListener(this.rdvMonitor);

        // Starting the monitor
        logJxta( peergroup, "Starting to monitor the peergroup " + peergroup.getPeerGroupName() );
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
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	@SuppressWarnings("unused")
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	protected void refresh(){
		if( Display.getDefault().isDisposed() )
			return;
		Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
            	if( rdvService == null )
            		return;

    	    	aliveRadioButton.setEnabled( isrunning );
    	    	isRDVCheckBox.setSelection(rdvService.isRendezVous());
            	isConnectedToRDVCheckBox.setSelection(rdvService.isConnectedToRendezVous());

            	List<PeerID> items = rdvService.getLocalRendezVousView();
            	// Sorting Peer IDs
            	List<String> StrItems = new ArrayList<String>();
            	for (int i=0;i<items.size();i++) 
            		StrItems.add(items.get(i).toString());
            	setInput( ConfigMode.RENDEZVOUS, StrItems );
            	items = rdvService.getLocalEdgeView();
            	// Sorting Peer IDs
            	StrItems = new ArrayList<String>();
            	for (int i=0;i<items.size();i++) 
            		StrItems.add(items.get(i).toString());
            	//statusPanel.updateRDVs( StrItems );
            	setInput( ConfigMode.EDGE, StrItems );
            	styledText.setText( rdvService.getImplAdvertisement().toString());
            //} else {
            //	logJxta( peerGroup, "Rendezvous service is NULL");
            }

		});		
	}

    private void IsConnectedToRDVCheckBoxActionPerformed(SelectionEvent e) {//GEN-FIRST:event_IsConnectedToRDVCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IsConnectedToRDVCheckBoxActionPerformed

    private void IsRDVCheckBoxActionPerformed(SelectionEvent e) {//GEN-FIRST:event_IsRDVCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IsRDVCheckBoxActionPerformed

    private synchronized void stopMonitorTask() {

        if ( theMonitorFuture != null ) {
            theMonitorFuture.cancel(false);
        }
    	this.isrunning = false;;
  }

    @Override
    protected void finalize() {
        stopMonitorTask();
        this.rdvService.removeListener( this.rdvMonitor);
    }

    public class RdvEventMonitor implements RendezvousListener {

    	@Override
		public void rendezvousEvent(RendezvousEvent event) {
     		if ( event == null ) 
    			return;
            String Log = null;
            //RendezVousService ddvs = (RendezVousService) event.getSource();

            if ( event.getType() == RendezvousEvent.RDVCONNECT ) {
                Log = "Connection to RDV";
            } else if ( event.getType() == RendezvousEvent.RDVRECONNECT ) {
                Log = "Reconnection to RDV";
            } else if ( event.getType() == RendezvousEvent.CLIENTCONNECT ) {
                Log = "EDGE client connection";
            } else if ( event.getType() == RendezvousEvent.CLIENTRECONNECT ) {
                Log = "EDGE client reconnection";
            } else if ( event.getType() == RendezvousEvent.RDVDISCONNECT ) {
                Log = "Disconnection from RDV";
            } else if ( event.getType() == RendezvousEvent.RDVFAILED ) {
                Log = "Connection to RDV failed";
            } else if ( event.getType() == RendezvousEvent.CLIENTDISCONNECT ) {
                Log = "EDGE client disconnection from RDV";
            } else if ( event.getType() == RendezvousEvent.CLIENTFAILED ) {
                Log = "EDGE client connection to RDV failed";
            } else if ( event.getType() == RendezvousEvent.BECAMERDV ) {
                Log = "This peer became RDV";
            } else if ( event.getType() == RendezvousEvent.BECAMEEDGE ) {
                Log = "This peer became EDGE";
            }

            String TempPID = event.getPeer();
            if ( TempPID != null ) Log = Log + "\n  " + TempPID;

            // Adding the entry
            //logJxta( ddvs.gpeerGroup, Log );

        }
    }
    
    private void logJxta( final PeerGroup peerGroup, final String message ){
    	if( Display.getDefault().isDisposed() )
    		return;
    	Display.getDefault().asyncExec(new Runnable() {
    		@Override
    		public void run() {
    			LogRecord record = new LogRecord( Jp2pLevel.getJxtaLevel(), message );
    			record.setSourceClassName( this.getClass().getName() );
    			Object[] parameters = new Object[2];
    			parameters[0] = peerGroup.getPeerName();
    			Color color = ColorUtils.getSWTColor( Display.getDefault(), SupportedColors.COLOR_CYAN );//coding.get( peerGroup ));
    			parameters[1] = color;
    			record.setParameters(parameters);
    			Jp2pLog.logJxta( record );
    		}
    	});
    }
}
