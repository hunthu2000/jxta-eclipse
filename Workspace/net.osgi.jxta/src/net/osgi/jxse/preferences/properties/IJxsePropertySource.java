package net.osgi.jxse.preferences.properties;

public interface IJxsePropertySource< T extends Enum<T>, U extends Enum<U>> {

	/**
	 * Get the component name
	 * @return
	 */
	public String getComponentName();
	
	/**
	 * Get the depth of the component (root = 0)
	 * @return
	 */
	public int getDepth();
	
	/**
	 * Get the default value for the property
	 * @param id
	 * @return
	 */
	public Object getDefault( T id );
	
	/**
	 * Get the current value for the given property
	 */
	public Object getProperty( T id );
	
	/**
	 * Set the value for the given property
	 * @param id
	 * @param value
	 * @return 
	 */
	public boolean setProperty( T id, Object value );
	
	/**
	 * Validate the given property
	 * @param id
	 * @return
	 */
	public boolean validate( T id, Object value );

	/**
	 * Get the default directives
	 * @param id
	 * @return
	 */
	public Object getDirective( U id );

	/**
	 * Get the default directives
	 * @param id
	 * @return
	 */
	public Object getDefaultDirectives( U id);
	
	/**
	 * Get the children of the property source
	 * @return
	 */
	public IJxsePropertySource<?,?>[] getChildren();

	boolean setDirective(U id, Object value); 
}
