package org.chaupal.jp2p.ui.template.config;

import net.jp2p.container.Jp2pContainerPropertySource;

import org.chaupal.jp2p.ui.jxta.network.Jp2pNetworkConfiguratorComposite;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class NetworkConfiguratorWizardPage extends WizardPage {

	private Jp2pNetworkConfiguratorComposite composite;
	
	protected NetworkConfiguratorWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Jp2pNetworkConfiguratorComposite( parent, SWT.NONE );
		super.setControl(composite);	
	}
	public void init( Jp2pContainerPropertySource source ){
		composite.init(source);
	}
}