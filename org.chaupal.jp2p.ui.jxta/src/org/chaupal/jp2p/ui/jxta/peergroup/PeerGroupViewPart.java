package org.chaupal.jp2p.ui.jxta.peergroup;

import net.jp2p.container.utils.INode;
import net.jp2p.container.utils.SimpleNode;
import net.jxta.peergroup.PeerGroup;

import org.chaupal.jp2p.ui.jxta.view.AbstractJp2pServiceViewPart;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.layout.TreeColumnLayout;

public class PeerGroupViewPart extends AbstractJp2pServiceViewPart<INode<PeerGroup,PeerGroup>>{

	public static final String ID = "org.chaupal.jp2p.ui.peergroup.view"; //$NON-NLS-1$
	public static final String S_PEERGROUP_VIEWER = "Peergroup Viewer";

	private Button aliveRadioButton;
	
 	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	private Button isConnectedToRelayCheckBox;
	private Text relayIDTextField;
	
	private TableViewer tableViewer;
	private StyledText styledText;
	
 	private Table table;
 	private SashForm sashForm_1;
 	private Composite composite;
 	private Tree tree;
 	private TreeViewer treeViewer;

	private ISelectionListener listener = new ISelectionListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			if (!( sourcepart.equals( this )))
				return;
			if(!( selection instanceof IStructuredSelection))
				return;
			
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object element = ss.getFirstElement();
			
			//We check for service decorators coming from the service navigator
			if(!( element instanceof SimpleNode<?,?>))
				return;
			SimpleNode<PeerGroup,PeerGroup> node = (SimpleNode<PeerGroup, PeerGroup> )element;
			setInput( node.getData() );
		}
	};


	public PeerGroupViewPart() {
		super( S_PEERGROUP_VIEWER );
	}


	@Override
	protected void onCreatePartControl(Composite parent) {		

		sashForm_1 = new SashForm(parent, SWT.NONE);
		toolkit.adapt(sashForm_1);
		toolkit.paintBordersFor(sashForm_1);

		composite = new Composite(sashForm_1, SWT.NONE);
		toolkit.adapt(composite);
		toolkit.paintBordersFor(composite);
		composite.setLayout(new TreeColumnLayout());

		treeViewer = new TreeViewer(composite, SWT.BORDER);
		treeViewer.setAutoExpandLevel( TreeViewer.ALL_LEVELS);
		treeViewer.setContentProvider( new PeerGroupContentProvider());
		treeViewer.setLabelProvider( new PeerGroupLabelProvider());
		tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		toolkit.paintBordersFor(tree);
		SashForm sashForm = new SashForm(sashForm_1, SWT.VERTICAL);
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
		sashForm_1.setWeights(new int[] {1, 1});
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
	}

	public void setInput( PeerGroup peergroup ){
		tableViewer.setInput( peergroup );
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected INode<PeerGroup,PeerGroup> onSetService(PeerGroup peergroup) {
		SimpleNode<PeerGroup,PeerGroup> leaf = new SimpleNode<PeerGroup, PeerGroup>( peergroup );
		PeerGroup current = peergroup;
		SimpleNode node = leaf;
		while( current.getParentGroup() != null ){
			current = current.getParentGroup();
			node = new SimpleNode<PeerGroup, PeerGroup>( current );
			node.addChild( leaf );
			leaf = node;
		}
		this.treeViewer.setInput(node);
		return node;
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	protected void onRefresh() {
		if( aliveRadioButton.isDisposed() )
			return;
		aliveRadioButton.setSelection( super.isRunning() );
		PeerGroup peergroup = super.getPeerGroup();
		String msg = peergroup.getPeerName();
		if( peergroup.getPeerID() != null )
			msg += "[" + peergroup.getPeerID().toString() + "]";
		setContentDescription( msg );
	}

	@Override
	protected void onFinalize() {
        stopMonitorTask();
	}

	private void IsConnectedToRelayCheckBoxActionPerformed( SelectionEvent evt) {//GEN-FIRST:event_IsConnectedToRelayCheckBoxActionPerformed
		// TODO add your handling code here:
	}
}