package org.eclipselabs.jxse.template.project;

import net.osgi.jxse.context.JxseContextPropertySource;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipselabs.jxse.ui.network.JxseNetworkConfiguratorComposite;

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
	public void init( JxseContextPropertySource source ){
		composite.init(source);
	}
}