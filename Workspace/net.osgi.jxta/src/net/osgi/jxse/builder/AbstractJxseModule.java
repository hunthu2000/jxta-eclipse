package net.osgi.jxse.builder;

import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.IJxseWritePropertySource;

public abstract class AbstractJxseModule<T extends Object, U extends IJxseWritePropertySource<IJxseProperties>> implements IJxseModule<T> {

	private U source;
	private IComponentFactory<T> factory;
	private IJxseModule<?> parent;
	private int weight;
		
	
	protected AbstractJxseModule() {
		super();
		this.weight = 0;
	}

	protected AbstractJxseModule( IJxseModule<?> parent ) {
		this( parent, Integer.MAX_VALUE );
	}

	protected AbstractJxseModule( IJxseModule<?> parent, int weight ) {
		this.parent = parent;
		this.weight = weight;
	}
	
	public int getWeight() {
		return weight;
	}

	protected void setWeight(int weight) {
		this.weight = weight;
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

	protected IJxseModule<?> getParent() {
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
	public IJxsePropertySource<IJxseProperties> createPropertySource() {
		if( this.source == null )
			this.source = this.onCreatePropertySource();
		return source;
	}

	
	@Override
	public void extendModules() { /* DO NOTHING */}		

	public IComponentFactory<T> getFactory() {
		return factory;
	}

	protected void setFactory(IComponentFactory<T> factory) {
		this.factory = factory;
	}

	/**
	 * Is called upon creating the property source.
	 * @return
	 */
	protected abstract IComponentFactory<T> onCreateFactory();
	
	@Override
	public IComponentFactory<T> createFactory() {
		if( this.factory == null )
			this.factory = this.onCreateFactory();
		return factory;
	}

	/**
	 * Returns true if the module can be created
	 * @return
	 */
	public boolean canCreate(){
		return ( source != null );
	}

	/**
	 * Returns true if the module is complete. this means that the factoryb was created succesfully
	 * @return
	 */
	public boolean isCompleted(){
		if( this.factory == null )
			return false;
		return factory.isCompleted();
	}

	/**
	 * Create the factory that will make the component
	 * @param provider
	 * @return
	 */
	public T getComponent(){
		return this.factory.getComponent();
	}

	/**
	 * Returns true if the component can be activated
	 * @return
	 */
	public boolean canActivate(){
		if(( this.factory == null ) || ( !factory.isCompleted() ))
			return false;
		return ( factory.getComponent() instanceof IActivator );
	}
}
