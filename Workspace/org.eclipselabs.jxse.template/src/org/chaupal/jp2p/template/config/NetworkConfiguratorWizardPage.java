package org.chaupal.jp2p.template.config;

import net.osgi.jp2p.context.Jp2pContainerPropertySource;

import org.chaupal.jp2p.ui.network.JxseNetworkConfiguratorComposite;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class NetworkConfiguratorWizardPage extends WizardPage {

	private JxseNetworkConfiguratorComposite composite;
	
	protected NetworkConfiguratorWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		composite = new JxseNetworkConfiguratorComposite( parent, SWT.NONE );
		super.setControl(composite);	
	}
	public void init( Jp2pContainerPropertySource source ){
		composite.init(source);
	}
}