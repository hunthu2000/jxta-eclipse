package net.osgi.jxse.seeds;

import net.osgi.jxse.builder.AbstractJxseModule;
import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.factory.IComponentFactory.Components;
import net.osgi.jxse.properties.IJxseDirectives.Directives;

public class SeedListModule extends AbstractJxseModule<String, SeedListPropertySource> {

	public SeedListModule(IJxseModule<?> parent) {
		super(parent);
	}

	@Override
	public String getComponentName() {
		return Components.SEED_LIST.toString();
	}
	
	@Override
	public boolean canCreate() {
		if( super.getPropertySource() == null )
			return false;
		SeedListPropertySource source = super.getPropertySource();
		return !SeedListPropertySource.getBoolean(source, Directives.BLOCK_CREATION);
	}

	
	@Override
	public boolean isCompleted() {
		if( super.getPropertySource() == null )
			return false;
		SeedListPropertySource source = super.getPropertySource();
		return SeedListPropertySource.getBoolean(source, Directives.BLOCK_CREATION);
	}

	@Override
	protected SeedListPropertySource onCreatePropertySource() {
		return new SeedListPropertySource( super.getParent().getPropertySource() );
	}

	@Override
	public IComponentFactory<String> onCreateFactory() {
		SeedListFactory factory = new SeedListFactory( super.getPropertySource() );
		return factory;
	}

	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
	}
}
