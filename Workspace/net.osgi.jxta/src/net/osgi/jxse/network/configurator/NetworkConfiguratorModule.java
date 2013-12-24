package net.osgi.jxse.network.configurator;

import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.builder.container.BuilderContainer;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.network.NetworkManagerPropertySource;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.seeds.ISeedListFactory;
import net.osgi.jxse.seeds.SeedListFactory;
import net.osgi.jxse.seeds.SeedListPropertySource;
import net.osgi.jxse.utils.StringStyler;

public class NetworkConfiguratorModule extends AbstractJxseModule<NetworkConfigurator, NetworkConfigurationPropertySource> {

	private Class<?> clss;
	
	private NetworkManager manager;
	
	public NetworkConfiguratorModule( Class<?> clss, IJxseModule<?> parent, BuilderContainer container ) {
		super(parent, 3, container);
		this.clss = clss;
	}

	@Override
	public String getComponentName() {
		return Components.NETWORK_CONFIGURATOR.toString();
	}

	@Override
	protected NetworkConfigurationPropertySource onCreatePropertySource() {
		NetworkConfigurationPropertySource source = new NetworkConfigurationPropertySource( (NetworkManagerPropertySource) super.getParent().getPropertySource() );
		SeedListPropertySource slps = new SeedListPropertySource( source, clss );
		if( slps.hasSeeds() )
			source.addChild(slps);
		return source;
	}

	
	@Override
	public boolean canCreate() {
		return ( this.manager != null );
	}

	@Override
	public IComponentFactory<NetworkConfigurator> onCreateFactory() {
		NetworkConfigurationFactory factory = new NetworkConfigurationFactory( super.getPropertySource(), manager );
		for( IJxsePropertySource<?> source: factory.getPropertySource().getChildren() ){
			if( source instanceof SeedListPropertySource ){
				SeedListFactory slf = new SeedListFactory( (SeedListPropertySource) source );
				factory.addSeedlist(slf);
			}
		}
		return factory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		String name = StringStyler.styleToEnum( event.getModule().getComponentName() );
		if( !Components.isComponent(name ))
			return;
		Components component = Components.valueOf(name);
		IComponentFactory<?> factory = null;
		switch( event.getBuilderEvent() ){
		case PROPERTY_SOURCE_CREATED:
			switch( component){
			case SEED_LIST:
				SeedListPropertySource source = (SeedListPropertySource) event.getModule().getPropertySource();
				source.setDirective( Directives.BLOCK_CREATION, Boolean.TRUE.toString());
				break;
			case TCP:
			case HTTP:
			case HTTP2:
			case SECURITY:
			case MULTICAST:
				break;
			default:
				break;
			}
			break;
		case FACTORY_CREATED:
			factory = event.getModule().getFactory();
			switch( component){
			case SEED_LIST:
				NetworkConfigurationFactory ncf = (NetworkConfigurationFactory) super.getFactory();
				ncf.addSeedlist((ISeedListFactory) factory );
				factory.complete();
				break;
			case TCP:
			case HTTP:
			case HTTP2:
			case SECURITY:
			case MULTICAST:
				break;
			default:
				break;
			}
			break;
		case COMPONENT_CREATED:
			if( Components.NETWORK_MANAGER.toString().equals( event.getModule().getComponentName() )){
				this.manager = (NetworkManager) event.getModule().getComponent();
				factory = this.createFactory();
				super.notifyListeners( new ComponentBuilderEvent(this, this, BuilderEvents.FACTORY_CREATED ));
				factory.createComponent();
				super.notifyListeners( new ComponentBuilderEvent(this, this, BuilderEvents.COMPONENT_CREATED ));
				factory.complete();
			}
			break;
		default:
			break;
		}
	}
}
