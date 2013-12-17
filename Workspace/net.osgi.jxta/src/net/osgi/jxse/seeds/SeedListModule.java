package net.osgi.jxse.seeds;

import net.osgi.jxse.component.AbstractJxseModule;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.peergroup.IPeerGroupProvider;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public class SeedListModule extends AbstractJxseModule<String, SeedListPropertySource> {

	public SeedListModule(IJxsePropertySource<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.SEED_LIST.toString();
	}


	@Override
	protected SeedListPropertySource onCreatePropertySource() {
		return new SeedListPropertySource( super.getParent() );
	}

	@Override
	public IComponentFactory<String, IJxseProperties> createFactory( IPeerGroupProvider provider ) {
		return new SeedListFactory( super.getPropertySource() );
	}
}
