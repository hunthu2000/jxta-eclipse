package org.eclipselabs.jxse.ui.network;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.FillLayout;

public class JxseNetworkConfiguratorComposite extends Composite {

	private TcpConfigurationComposite tcpComposite;
	private HttpConfigurationComposite httpComposite;
	private MulticastConfigurationComposite multicastComposite;
	private RdvRelayConfigurationComposite rdvRelayComposite;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public JxseNetworkConfiguratorComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		
		Composite composite = new OverviewConfigurationComposite(sashForm, SWT.NONE);
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
		
		TabItem tbtmTcpItem = new TabItem(tabFolder, SWT.NONE);
		tbtmTcpItem.setText("Tcp Configuration");
		tcpComposite = new TcpConfigurationComposite( tabFolder, SWT.NONE);
		tbtmTcpItem.setControl(tcpComposite);
		sashForm.setWeights(new int[] {1, 1});
		
		TabItem tbtmHttpItem = new TabItem(tabFolder, SWT.NONE);
		tbtmHttpItem.setText("Http Configuration");
		httpComposite = new HttpConfigurationComposite( tabFolder, SWT.NONE);
		tbtmHttpItem.setControl(httpComposite);
		sashForm.setWeights(new int[] {1, 1});
		
		TabItem tbtmMulticastItem = new TabItem(tabFolder, SWT.NONE);
		tbtmMulticastItem.setText("Multicast");
		multicastComposite = new MulticastConfigurationComposite( tabFolder, SWT.NONE);
		tbtmMulticastItem.setControl(multicastComposite);
		sashForm.setWeights(new int[] {1, 1});

		TabItem tbtmRdvRelayItem = new TabItem(tabFolder, SWT.NONE);
		tbtmRdvRelayItem.setText("Relay & Rendezvous");
		rdvRelayComposite = new RdvRelayConfigurationComposite( tabFolder, SWT.NONE);
		tbtmRdvRelayItem.setControl(rdvRelayComposite);
		sashForm.setWeights(new int[] {1, 1});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
