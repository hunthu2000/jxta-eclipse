package net.osgi.jxse.builder;

import net.osgi.jxse.activator.IActivator;
import net.osgi.jxse.builder.container.BuilderContainer;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.factory.ComponentBuilderEvent;
import net.osgi.jxse.factory.IComponentFactory;
import net.osgi.jxse.properties.IJxseProperties;
import net.osgi.jxse.properties.IJxsePropertySource;

public abstract class AbstractJxseModule<T extends Object, U extends IJxsePropertySource<IJxseProperties>> implements IJxseModule<T> {

	private U source;
	private IComponentFactory<T> factory;
	private IJxseModule<?> parent;
	private int weight;
	private BuilderContainer container;
	
	protected AbstractJxseModule() {
		super();
		this.weight = 0;
	}

	protected AbstractJxseModule( IJxseModule<?> parent ) {
		this( parent, Integer.MAX_VALUE );
	}

	protected AbstractJxseModule( IJxseModule<?> parent, BuilderContainer container ) {
		this( parent, Integer.MAX_VALUE );
		this.container = container;
	}

	protected AbstractJxseModule( IJxseModule<?> parent, int weight ) {
		this.parent = parent;
		this.weight = weight;
	}

	protected AbstractJxseModule( IJxseModule<?> parent, int weight, BuilderContainer container ) {
		this( parent, weight );
		this.container = container;
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

	public IJxseModule<?> getParent() {
		return parent;
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

	/**
	 * Notify the container of a change in the state
	 * @param event
	 */
	protected void notifyListeners( ComponentBuilderEvent<Object> event ){
		if( this.container == null )
			return;
		this.container.notifyChange(event);
	}
	
	@Override
	public void notifyChange(ComponentBuilderEvent<Object> event) {
		if( event.getSource().equals( this ))
			return;
		if( !BuilderEvents.COMPONENT_CREATED.equals( event.getBuilderEvent()))
			return;
		IJxseModule<?>parent = event.getModule().getParent();
		if(( parent == null ) || (!parent.equals( this )))
			return;
		if(!( parent.getComponent() instanceof IJxseComponentNode ))
			return;
		IJxseComponentNode<?> component = (IJxseComponentNode<?>) parent.getComponent();
		component.addChild((IJxseComponent<?, ?>) this.factory.getComponent());	
	}	
}
