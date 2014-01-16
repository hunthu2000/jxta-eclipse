package org.chaupal.jp2p.ui.endpoint;

import net.jxta.endpoint.EndpointService;
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
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

import java.util.ArrayList;
import java.util.Collection;
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

public class EndPointViewPart extends ViewPart{

	public static final String ID = "net.equinox.jxta.ui.endpoint.EndPointViewPart"; //$NON-NLS-1$
	public static final String S_ENDPOINT_VIEWER = "End Point Viewer";

	private Button aliveRadioButton;
	
   private RdvEventMonitor rdvMonitor;
    
    private Future<?> theMonitorFuture = null;
	public static final ScheduledExecutorService theExecutor = Executors.newScheduledThreadPool(5);

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
	private Button isConnectedToRelayCheckBox;
	private Text relayIDTextField;
	
	private TableViewer tableViewer;
	private StyledText styledText;
	
    private PeerGroup peerGroup = null;
    
	private Table table;
		
	public EndPointViewPart() {
		setPartName( S_ENDPOINT_VIEWER );
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
				if ( sourcepart instanceof EndPointViewPart )
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
						
								isConnectedToRelayCheckBox = new Button(composite_2, SWT.RADIO);
								isConnectedToRelayCheckBox.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent e) {
										IsConnectedToRelayCheckBoxActionPerformed( e );
									}
								});
								isConnectedToRelayCheckBox.setText("is connected to Relay");
								Label lblRelayId = new Label(composite_2, SWT.NONE);
								lblRelayId.setText("Relay ID");
								
										relayIDTextField = new Text(composite_2, SWT.BORDER);
										relayIDTextField.setEditable(false);
										relayIDTextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite tableComposite = new Composite(composite_2, SWT.NONE);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		
		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		TableViewerColumn column = createColumn("Edges", tableViewer);
		tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(100, 200, true)); 		
		tableViewer.setColumnProperties(new String[] {"Relays"});
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		styledText = new StyledText(sashForm, SWT.BORDER);
		toolkit.adapt(styledText);
		toolkit.paintBordersFor(styledText);
		
		sashForm.setWeights(new int[] {3, 1});

		createActions();
		initializeToolBar();
		initializeMenu();
        theMonitorFuture = theExecutor.scheduleAtFixedRate(runner, 5, 1, TimeUnit.SECONDS);
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
	}

	private TableViewerColumn createColumn( String name, TableViewer viewer ){
		TableViewerColumn col = createTableViewerColumn( viewer, name, 100, 0 );
		col.setLabelProvider(new ColumnLabelProvider() {
		  @Override
		  public String getText(Object element) {
		    return super.getText(element);
		  }
		});		
		return col;
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
			tableViewer.setInput( values );
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
			this.setPeerGroup(null );
		else
		  this.setPeerGroup( (PeerGroup) component.getModule());
	}

    /** Creates new form ConnectivityMonitor */
    private void setPeerGroup(final PeerGroup inGroup) {

        // Initialization
        peerGroup = inGroup;
        if( peerGroup == null )
        	 return;
         
         // Registering as rendezvous event listener
         if( this.rdvMonitor != null )
         	inGroup.getRendezVousService().removeListener(this.rdvMonitor );
         this.rdvMonitor = new RdvEventMonitor(this);
         inGroup.getRendezVousService().addListener(this.rdvMonitor);

        // Starting the monitor
        logJxta( peerGroup, "Starting to monitor the peergroup " + peerGroup.getPeerGroupName() );
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
                if(( peerGroup == null ) || ( aliveRadioButton.isDisposed() ))
                	return;
                aliveRadioButton.setEnabled( isrunning );
            	String msg = peerGroup.getPeerName();
                if( peerGroup.getPeerID() != null )
                	msg += "[" + peerGroup.getPeerID().toString() + "]";
            	setContentDescription( msg );
                   
                EndpointService endpointService =peerGroup.getEndpointService();
                styledText.setText( endpointService.getImplAdvertisement().toString());
                if ( endpointService != null ) {
                    Collection<PeerID> x = endpointService.getConnectedRelayPeers();
                    if ( x.isEmpty() ) {
                        isConnectedToRelayCheckBox.setSelection(false);
                        relayIDTextField.setText("");
                    } else {
                        isConnectedToRelayCheckBox.setSelection(true);
                        PeerID[] TmpPID = x.toArray(new PeerID[x.size()]);
                        relayIDTextField.setText(TmpPID[0].toString());
                    }

                    Collection<PeerID> items = endpointService.getConnectedRelayPeers();
                    // Sorting Peer IDs
                    List<String> strItems = new ArrayList<String>();
                    for( PeerID item: items ) 
                        strItems.add( item.toString());

                    //statusPanel.updateEdges( StrItems );
                    setInput( ConfigMode.RELAY, strItems );

                	logJxta( peerGroup, "Endpoint service started");
                }
            }
         });			
	}

    private void IsConnectedToRelayCheckBoxActionPerformed( SelectionEvent evt) {//GEN-FIRST:event_IsConnectedToRelayCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IsConnectedToRelayCheckBoxActionPerformed

    private synchronized void stopMonitorTask() {

        if ( theMonitorFuture != null ) {
            theMonitorFuture.cancel(false);
        }
    }

    @Override
    protected void finalize() {
        stopMonitorTask();
    }

    public class RdvEventMonitor implements RendezvousListener {

        public RdvEventMonitor( EndPointViewPart inCM) {
        }

        @Override
		public void rendezvousEvent(RendezvousEvent event) {
            if ( event == null ) return;
            String Log = null;

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
            logJxta( peerGroup, Log );

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
