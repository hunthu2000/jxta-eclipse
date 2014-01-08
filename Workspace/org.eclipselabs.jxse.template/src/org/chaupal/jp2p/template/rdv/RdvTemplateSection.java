package org.chaupal.jp2p.template.rdv;

import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jp2p.container.Jp2pContainerPropertySource;
import net.osgi.jp2p.container.IJxseServiceContainer.ContextProperties;
import net.osgi.jp2p.jxta.network.NetworkManagerPropertySource;
import net.osgi.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource;
import net.osgi.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;

import org.chaupal.jp2p.template.config.JxseConfigurationBundleSection;
import org.chaupal.jp2p.template.project.ContextWizardOption.TemplateOptions;
import org.eclipse.pde.ui.templates.ITemplateSection;

public class RdvTemplateSection extends JxseConfigurationBundleSection implements
		ITemplateSection {

	public RdvTemplateSection() {
		super();
	}

	@Override
	protected void onFillProperties(Jp2pContainerPropertySource properties) {
		properties.setProperty(ContextProperties.CONFIG_MODE, ConfigMode.RENDEZVOUS);
		NetworkManagerPropertySource nmps = new NetworkManagerPropertySource( properties );
		nmps.setDirective( Directives.CLEAR, "true");
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
