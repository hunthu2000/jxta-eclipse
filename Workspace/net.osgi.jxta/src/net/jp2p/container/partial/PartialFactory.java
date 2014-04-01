package net.jp2p.container.partial;

import net.jp2p.container.factory.AbstractPropertySourceFactory;

public class PartialFactory<T extends Object> extends AbstractPropertySourceFactory {

	private String componentName;
	
	public PartialFactory( String componentName ){
		this.componentName = componentName;
	}

	@Override
	public String getComponentName() {
		return componentName;
	}

	@Override
	protected PartialPropertySource onCreatePropertySource() {
		return new PartialPropertySource( this.componentName, super.getParentSource() );
	}
}
