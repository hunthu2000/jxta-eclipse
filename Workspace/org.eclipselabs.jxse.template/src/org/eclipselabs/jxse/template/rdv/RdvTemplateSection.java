package org.eclipselabs.jxse.template.rdv;

import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.context.JxseContextPropertySource;
import net.osgi.jxse.network.configurator.NetworkConfigurationPropertySource;
import net.osgi.jxse.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;

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
		nmps.setDirective( Directives.CLEAR_CONFIG, "true");
		nmps.setDirective( Directives.AUTO_START, "true");
		properties.addChild(nmps);
		NetworkConfigurationPropertySource ncps = new NetworkConfigurationPropertySource( nmps );
		ncps.setProperty(NetworkConfiguratorProperties.TCP_8ENABLED, true );
		ncps.setProperty(NetworkConfiguratorProperties.TCP_8INCOMING_STATUS, true );
		ncps.setProperty(NetworkConfiguratorProperties.TCP_8OUTGOING_STATUS, true );
		ncps.setProperty(NetworkConfiguratorProperties.MULTICAST_8ENABLED, false );
		nmps.addChild(ncps);
		super.onFillProperties(properties);
		super.setTemplateOption( TemplateOptions.SIMPLE_RDV);
	}
}
