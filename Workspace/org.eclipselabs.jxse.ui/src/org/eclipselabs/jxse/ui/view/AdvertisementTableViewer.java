package org.eclipselabs.jxse.ui.view;

import net.jxta.document.Advertisement;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.utils.StringStyler;

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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.layout.FillLayout;
import org.eclipselabs.jxse.ui.monitor.ConnectivityViewPart;

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
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		createColumn( AdvertisementColumns.TYPE.toString(), tableViewer);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		toolkit.paintBordersFor(table);

		createColumn(AdvertisementColumns.NAME.toString(), tableViewer);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		toolkit.paintBordersFor(table);

		createColumn(AdvertisementColumns.ID.name(), tableViewer);
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
		if(!( selection instanceof IStructuredSelection))
			return;
		
		IStructuredSelection ss = (IStructuredSelection) selection;
		Object element = ss.getFirstElement();
		
		//We check for service decorators coming from the service navigator
		if(!( element instanceof IJxseComponent<?,?>))
			return;
		IJxseComponent<?,?> component = (IJxseComponent<?,?> )element;
		this.tableViewer.setInput( component.getAdvertisements() );
	}

	@Override
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	private void createColumn( final String name, TableViewer viewer ){
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
