package net.osgi.jxse.seeds;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;

public class SeedListModule extends AbstractJxseModule<String, SeedListPropertySource> {

	public SeedListModule(IJxseModule<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.SEED_LIST.toString();
	}


	@Override
	protected SeedListPropertySource onCreatePropertySource() {
		return new SeedListPropertySource( super.getParent().getPropertySource() );
	}

	@Override
	public IComponentFactory<String> onCreateFactory() {
		return new SeedListFactory( super.getPropertySource() );
	}

	@Override
	public void notifyCreated(ComponentBuilderEvent<Object> event) {
		// TODO Auto-generated method stub
		
	}
}
