package org.eclipselabs.jxse.template.project;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipselabs.jxse.ui.network.JxseNetworkConfiguratorComposite;

public class NetworkConfiguratorWizardPage extends WizardPage {

	private JxseNetworkConfiguratorComposite tcpComposite;
	
	protected NetworkConfiguratorWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		tcpComposite = new JxseNetworkConfiguratorComposite( parent, SWT.NONE );
		super.setControl(tcpComposite);	
	}
}