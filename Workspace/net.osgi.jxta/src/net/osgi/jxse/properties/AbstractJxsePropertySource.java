/*******************************************************************************
 * Copyright (c) 2013 Condast and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kees Pieters - initial API and implementation
 *******************************************************************************/
package net.osgi.jxse.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.osgi.jxse.properties.IJxseDirectives.Directives;
import net.osgi.jxse.utils.StringDirective;
import net.osgi.jxse.utils.StringProperty;
import net.osgi.jxse.utils.Utils;

public abstract class AbstractJxsePropertySource implements IJxsePropertySource<IJxseProperties> {
	
	public static final String S_RUNTIME = "Runtime"; //used for runtime properties
			
	private Map<IJxseProperties,ManagedProperty<IJxseProperties,Object>> properties;
	private Map<IJxseDirectives,String> directives;
	
	private IJxsePropertySource<?> parent;

	private Collection<IJxsePropertySource<?>> children;

	private int depth = 0;
	private String id_root;
	private String bundleId, componentName;
	
	public AbstractJxsePropertySource( String bundleId, String identifier, String componentName) {
		this( bundleId, identifier, componentName, 0);
	}

	protected AbstractJxsePropertySource( String bundleId, String identifier, String componentName, int depth ) {
		properties = new HashMap<IJxseProperties,ManagedProperty<IJxseProperties,Object>>();
		directives = new HashMap<IJxseDirectives,String>();
		this.bundleId = bundleId;
		this.directives.put( IJxseDirectives.Directives.ID, this.bundleId + "." + componentName.toLowerCase() );
		this.id_root = this.bundleId;
		this.directives.put( IJxseDirectives.Directives.NAME, identifier );
		this.componentName = componentName;
		children = new ArrayList<IJxsePropertySource<?>>();
		this.depth = depth;
		this.parent = null;
	}

	protected AbstractJxsePropertySource( IJxsePropertySource<?> parent ) {
		this( parent.getBundleId(), parent.getIdentifier(), parent.getComponentName(), parent.getDepth() + 1 );
		this.parent = parent;
	}

	protected AbstractJxsePropertySource( String componentName, IJxsePropertySource<?> parent ) {
		this( parent );
		this.componentName = componentName;
		this.directives.put( IJxseDirectives.Directives.CONTEXT, parent.getDirective(IJxseDirectives.Directives.CONTEXT) );
	}

	public IJxsePropertySource<?> getParent(){
		return this.parent;
	}

	public String getId() {
		return (String) this.directives.get( IJxseDirectives.Directives.ID );
	}

	public String getIdRoot() {
		return id_root;
	}

	@Override
	public String getBundleId() {
		return this.bundleId;
	}

	@Override
	public String getIdentifier() {
		return (String) this.directives.get( IJxseDirectives.Directives.NAME );
	}

	@Override
	public String getComponentName() {
		return this.componentName;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public Object getProperty(IJxseProperties id) {
		ManagedProperty<IJxseProperties,Object> select = this.getManagedProperty(id);
		if( select == null )
			return null;
		return select.getValue();
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( IJxseProperties id ){
		ManagedProperty<IJxseProperties,Object> select = this.getManagedProperty(id);
		return select.getCategory();
		
	}

	protected boolean setManagedProperty( ManagedProperty<IJxseProperties,Object> property ) {
		this.properties.put( property.getKey(), property );
		return true;
	}
		
	@Override
	public Object getDefault(IJxseProperties id) {
		ManagedProperty<IJxseProperties,Object> select = this.getManagedProperty(id);
		if( select == null )
			return null;
		return select.getDefaultValue();
	}

	public ManagedProperty<IJxseProperties,Object> getManagedProperty( IJxseProperties id ){
		return properties.get( id );
	}
	
	@Override
	public Iterator<IJxseProperties> propertyIterator() {
		return this.properties.keySet().iterator();
	}

	@Override
	public String getDirective( IJxseDirectives id) {
		return directives.get( id );
	}

	/**
	 * Set the directive
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean setDirective( IJxseDirectives id, String value) {
		if( value == null )
			return false;
		if( id instanceof StringDirective )
			directives.put( Directives.valueOf(id.name() ), value );
		else
			directives.put( id, value );
		return true;
	}

	@Override
	public Iterator<IJxseDirectives> directiveIterator() {
		return directives.keySet().iterator();
	}

	@Override
	public boolean addChild( IJxsePropertySource<?> child ){
		return this.children.add( child );
	}

	@Override
	public void removeChild( IJxsePropertySource<?> child ){
		this.children.remove( child );
	}

	public IJxsePropertySource<?> getChild( String componentName ){
		for( IJxsePropertySource<?> child: this.children ){
			if( child.getComponentName().equals(componentName ))
				return child;
		}
		return null;
	}

	@Override
	public IJxsePropertySource<?>[] getChildren() {
		return this.children.toArray(new IJxsePropertySource[children.size()]);
	}
	
	public boolean isEmpty(){
		return this.properties.isEmpty();
	}

	@Override
	public IJxseProperties getIdFromString(String key) {
		return new StringProperty( key );
	}

	@Override
	public boolean validate(IJxseProperties id, Object value) {
		return false;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + this.getComponentName() + "]";
	}
	
	/**
	 * If true, the service is autostarted
	 * @return
	 */
	public static boolean isAutoStart( IJxsePropertySource<?> source ){
		return Boolean.parseBoolean( (String) source.getDirective( IJxseDirectives.Directives.AUTO_START ));		
	}

	/**
	 * Set the parent directive to match that of the child, if the child's direcvtive is set and the parent's is null
	 * @param id
	 * @param source
	 */
	protected static void setDirectiveFromParent( IJxseDirectives id, IJxseWritePropertySource<?> source ) {
		String directive = source.getParent().getDirective( id );
		source.setDirective(id, directive);
	}

	/**
	 * Set the parent directive to match that of the child, if the child's direcvtive is set and the parent's is null
	 * @param id
	 * @param source
	 */
	public static void setParentDirective( IJxseDirectives id, IJxsePropertySource<?> source ) {
		Object directive = source.getDirective( id );
		IJxseWritePropertySource<?> parent = (IJxseWritePropertySource<?>) source.getParent();
		Object parent_autostart = parent.getDirective( id );
		if(( directive != null ) && ( parent_autostart == null ))
			parent.setDirective( id, Boolean.TRUE.toString());
	}
	
	/**
	 * Find the property source with the given component name, starting from the given source
	 * @param source
	 * @param componentName
	 * @return
	 */
	public static  IJxsePropertySource<?> findPropertySource( IJxsePropertySource<?> source, String componentName ){
		if( Utils.isNull( componentName ))
			return null;
		if( componentName.equals( source.getComponentName()))
			return source;
		IJxsePropertySource<?> result;
		for( IJxsePropertySource<?> child: source.getChildren() ){
			result = findPropertySource(child, componentName);
			if( result != null )
				return result;
		}
		return null;			
	}
	
	/**
	 * Get a boolean value for the given directive
	 * @param source
	 * @param id
	 * @return
	 */
	public static boolean getBoolean( IJxsePropertySource<?> source, IJxseDirectives id ){
		String directive = source.getDirective(id);
		if( Utils.isNull( directive ))
			return false;
		return Boolean.parseBoolean( directive);
	}
}