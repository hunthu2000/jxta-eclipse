package org.chaupal.jp2p.ui.advertisement;

import net.jxta.document.Advertisement;
import net.osgi.jp2p.jxta.advertisement.IAdvertisementProvider;

import org.chaupal.jp2p.ui.monitor.ConnectivityViewPart;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipselabs.osgi.ds.broker.util.StringStyler;

public class AdvertisementTableViewer extends ViewPart {

	public static final String ID = "net.equinox.jxta.ui.view.AdvertisementTableViewer"; //$NON-NLS-1$
	
	static enum AdvertisementColumns{
		ID,
		NAME,
		TYPE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Table table;
	private TableViewer tableViewer;

	private ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			// we ignore our own selections
			if ( sourcepart instanceof ConnectivityViewPart )
				return;
			showSelection( sourcepart, selection);
		}
	};

	public AdvertisementTableViewer() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		container.setLayout(tableColumnLayout);
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		TableViewerColumn column = createColumn( AdvertisementColumns.TYPE.toString(), tableViewer);
		tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(20, 20, true)); 		
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		toolkit.paintBordersFor(table);

		column = createColumn(AdvertisementColumns.NAME.toString(), tableViewer);
		tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(20, 20, true)); 		
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		toolkit.paintBordersFor(table);

		column = createColumn(AdvertisementColumns.ID.name(), tableViewer);
		tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(60, 200, true)); 		
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		toolkit.paintBordersFor(table);

		createActions();
		initializeToolBar();
		initializeMenu();

		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
		getSite().setSelectionProvider(this.tableViewer );
	}

	/**
	 * Shows the given selection in this view.
	 */
	void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
		if(!( selection instanceof TreeSelection))
			return;
		
		TreeSelection ss = (TreeSelection) selection;
		Object element = ss.getFirstElement();
		if(!( element instanceof IAdvertisementProvider ))
			return;
		IAdvertisementProvider provider = (IAdvertisementProvider) element;
		this.tableViewer.setInput( provider.getAdvertisements() );
	}

	@Override
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	private TableViewerColumn createColumn( final String name, TableViewer viewer ){
		TableViewerColumn col = createTableViewerColumn( viewer, name, 100, 0 );
		col.setLabelProvider(new ColumnLabelProvider() {
			
			
			@Override
		  public String getText(Object element) {
		    if(!( element instanceof Advertisement ))
		    	return super.getText(element);
		    Advertisement adv = ( Advertisement )element;
		    AdvertisementTableViewer.AdvertisementColumns column = AdvertisementColumns.valueOf( name.toUpperCase() );
		    switch( column ){
		    case ID:
		    	if( adv.getID() == null )
		    		return super.getText(element);
		    	return adv.getID().toString();
		    case NAME:
		    	return adv.getAdvType();
		    case TYPE:
		    	return adv.getAdvType();
		    }
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
	private void initializeMenu() {
		@SuppressWarnings("unused")
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
