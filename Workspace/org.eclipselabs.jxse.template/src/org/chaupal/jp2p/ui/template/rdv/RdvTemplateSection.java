package org.chaupal.jp2p.ui.template.rdv;

import net.jp2p.container.Jp2pContainerPropertySource;
import net.jp2p.container.properties.IJp2pDirectives.Directives;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jp2p.jxta.network.NetworkManagerPropertySource;
import net.jp2p.jxta.network.NetworkManagerPropertySource.NetworkManagerProperties;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource;
import net.jp2p.jxta.network.configurator.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;

import org.chaupal.jp2p.ui.template.config.JxseConfigurationBundleSection;
import org.chaupal.jp2p.ui.template.project.ContextWizardOption.TemplateOptions;
import org.eclipse.pde.ui.templates.ITemplateSection;

public class RdvTemplateSection extends JxseConfigurationBundleSection implements
		ITemplateSection {

	public RdvTemplateSection() {
		super();
	}

	@Override
	protected void onFillProperties(Jp2pContainerPropertySource properties) {
		NetworkManagerPropertySource nmps = new NetworkManagerPropertySource( properties );
		nmps.setProperty( NetworkManagerProperties.CONFIG_MODE, ConfigMode.RENDEZVOUS);
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
