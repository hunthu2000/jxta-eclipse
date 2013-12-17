package net.osgi.jxse.component;

import net.osgi.jxse.builder.IJxseModule;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public abstract class AbstractJxseModule<T extends Object, U extends IJxseWritePropertySource<IJxseProperties>> implements IJxseModule<T> {

	private U source;
	private IJxsePropertySource<?> parent;
		
	
	public AbstractJxseModule() {
		super();
	}

	public AbstractJxseModule( IJxsePropertySource<?> parent ) {
		this.parent = parent;
	}
		
	/**
	 * Get the property source that is used for the factor, or null if it wasn't created yet
	 * @return
	 */
	public U getPropertySource(){
		return this.source;
	}

	
	protected void setSource(U source) {
		this.source = source;
	}

	protected IJxsePropertySource<?> getParent() {
		return parent;
	}

	@Override
	public void setProperty(IJxseProperties id, Object value) {
		this.source.setProperty(id, value);	
	}
	
	/**
	 * Is called upon creating the property source.
	 * @return
	 */
	protected abstract U onCreatePropertySource();
	
	@Override
	public IJxsePropertySource<IJxseProperties> createPropertySource( IJxsePropertySource<?> parent ) {
		if( this.source == null )
			this.source = this.onCreatePropertySource();
		return source;
	}	
}
