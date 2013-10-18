package org.eclipselabs.jxse.template.rdv;

import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.network.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.network.NetworkManagerPropertySource;

import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipselabs.jxse.template.config.JxseConfigurationBundleSection;
import org.eclipselabs.jxse.template.project.ContextWizardOption.TemplateOptions;

public class RdvTemplateSection extends JxseConfigurationBundleSection implements
		ITemplateSection {

	public RdvTemplateSection() {
		super();
	}

	@Override
	protected void onFillProperties(JxseContextPropertySource properties) {
		properties.setProperty(ContextProperties.CONFIG_MODE, ConfigMode.RENDEZVOUS);
		NetworkManagerPropertySource nmps = new NetworkManagerPropertySource( properties );
		properties.addChild(nmps);
		NetworkConfigurationPropertySource ncps = new NetworkConfigurationPropertySource( nmps );
		ncps.setProperty(NetworkConfiguratorProperties.TCP_8ENABLED, true );
		ncps.setProperty(NetworkConfiguratorProperties.HTTP_8ENABLED, true );
		nmps.addChild(ncps);
		super.onFillProperties(properties);
		super.setTemplateOption( TemplateOptions.SIMPLE_RDV);
	}
}