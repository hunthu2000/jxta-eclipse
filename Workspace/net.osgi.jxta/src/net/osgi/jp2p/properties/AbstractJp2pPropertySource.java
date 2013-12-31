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
package net.osgi.jp2p.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.osgi.jp2p.properties.IJp2pDirectives.Contexts;
import net.osgi.jp2p.properties.IJp2pDirectives.Directives;
import net.osgi.jp2p.utils.SimpleComparator;
import net.osgi.jp2p.utils.StringDirective;
import net.osgi.jp2p.utils.StringProperty;
import net.osgi.jp2p.utils.StringStyler;
import net.osgi.jp2p.utils.Utils;

public abstract class AbstractJp2pPropertySource implements IJp2pPropertySource<IJp2pProperties> {
	
	public static final String S_RUNTIME = "Runtime"; //used for runtime properties
	public static final String S_DIRECTIVES = "Directives"; 
			
	private Map<IJp2pProperties,ManagedProperty<IJp2pProperties,Object>> properties;
	private Map<IJp2pDirectives,String> directives;
	
	private IJp2pPropertySource<?> parent;

	private Collection<IJp2pPropertySource<?>> children;

	private int depth = 0;
	private String id_root;
	private String bundleId, componentName;
	
	public AbstractJp2pPropertySource( String bundleId, String componentName) {
		this( bundleId, componentName, 0);
	}

	protected AbstractJp2pPropertySource( String bundleId, String componentName, int depth ) {
		properties = new TreeMap<IJp2pProperties,ManagedProperty<IJp2pProperties,Object>>( new SimpleComparator<IJp2pProperties>());
		directives = new TreeMap<IJp2pDirectives,String>(new SimpleComparator<IJp2pDirectives>());
		this.bundleId = bundleId;
		this.directives.put( IJp2pDirectives.Directives.ID, this.bundleId + "." + componentName.toLowerCase() );
		this.id_root = this.bundleId;
		this.componentName = componentName;
		children = new ArrayList<IJp2pPropertySource<?>>();
		this.depth = depth;
		this.parent = null;
	}

	protected AbstractJp2pPropertySource( String componentName, IJp2pPropertySource<?> parent ) {
		this( parent.getBundleId(), parent.getComponentName(), parent.getDepth() + 1 );
		this.componentName = componentName;
		this.parent = parent;
		this.directives.put( IJp2pDirectives.Directives.AUTO_START, parent.getDirective(IJp2pDirectives.Directives.AUTO_START ));
		this.directives.put( IJp2pDirectives.Directives.CONTEXT, parent.getDirective(IJp2pDirectives.Directives.CONTEXT ));
		this.directives.put( IJp2pDirectives.Directives.ID, parent.getId() + "." + componentName.toLowerCase() );
	}

	public IJp2pPropertySource<?> getParent(){
		return this.parent;
	}

	public String getId() {
		return (String) this.directives.get( IJp2pDirectives.Directives.ID );
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
		return (String) this.directives.get( IJp2pDirectives.Directives.NAME );
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
	public Object getProperty(IJp2pProperties id) {
		ManagedProperty<IJp2pProperties,Object> select = this.getManagedProperty(id);
		if( select == null )
			return null;
		return select.getValue();
	}

	/**
	 * Get the category for the given key
	 * @param key
	 * @return
	 */
	public String getCategory( IJp2pProperties id ){
		ManagedProperty<IJp2pProperties,Object> select = this.getManagedProperty(id);
		return select.getCategory();
		
	}

	protected boolean setManagedProperty( ManagedProperty<IJp2pProperties,Object> property ) {
		this.properties.put( property.getKey(), property );
		return true;
	}
		
	@Override
	public Object getDefault(IJp2pProperties id) {
		ManagedProperty<IJp2pProperties,Object> select = this.getManagedProperty(id);
		if( select == null )
			return null;
		return select.getDefaultValue();
	}

	public ManagedProperty<IJp2pProperties,Object> getManagedProperty( IJp2pProperties id ){
		return properties.get( id );
	}
	
	@Override
	public Iterator<IJp2pProperties> propertyIterator() {
		return this.properties.keySet().iterator();
	}

	@Override
	public String getDirective( IJp2pDirectives id) {
		return directives.get( id );
	}

	/**
	 * Set the directive
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean setDirective( IJp2pDirectives id, String value) {
		if( value == null )
			return false;
		if( id instanceof StringDirective )
			directives.put( Directives.valueOf(id.name() ), value );
		else
			directives.put( id, value );
		return true;
	}

	@Override
	public Iterator<IJp2pDirectives> directiveIterator() {
		return directives.keySet().iterator();
	}

	@Override
	public boolean addChild( IJp2pPropertySource<?> child ){
		return this.children.add( child );
	}

	@Override
	public void removeChild( IJp2pPropertySource<?> child ){
		this.children.remove( child );
	}

	public IJp2pPropertySource<?> getChild( String componentName ){
		for( IJp2pPropertySource<?> child: this.children ){
			if( child.getComponentName().equals(componentName ))
				return child;
		}
		return null;
	}

	@Override
	public IJp2pPropertySource<?>[] getChildren() {
		return this.children.toArray(new IJp2pPropertySource[children.size()]);
	}
	
	public boolean isEmpty(){
		return this.properties.isEmpty();
	}

	@Override
	public IJp2pProperties getIdFromString(String key) {
		return new StringProperty( key );
	}

	@Override
	public boolean validate(IJp2pProperties id, Object value) {
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
	public static boolean isAutoStart( IJp2pPropertySource<?> source ){
		return Boolean.parseBoolean( (String) source.getDirective( IJp2pDirectives.Directives.AUTO_START ));		
	}

	/**
	 * Set the parent directive to match that of the child, if the child's direcvtive is set and the parent's is null
	 * @param id
	 * @param source
	 * @return
	 */
	protected static boolean setDirectiveFromParent( IJp2pDirectives id, IJp2pWritePropertySource<?> source ) {
		String directive = source.getParent().getDirective( id );
		if( Utils.isNull( directive ))
			return false;
		return source.setDirective(id, directive);
	}

	/**
	 * Set the parent directive to match that of the child, if the child's direcvtive is set and the parent's is null
	 * @param id
	 * @param source
	 */
	public static void setParentDirective( IJp2pDirectives id, IJp2pPropertySource<?> source ) {
		Object directive = source.getDirective( id );
		IJp2pWritePropertySource<?> parent = (IJp2pWritePropertySource<?>) source.getParent();
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
	public static  IJp2pPropertySource<?> findPropertySource( IJp2pPropertySource<?> source, String componentName ){
		if( Utils.isNull( componentName ))
			return null;
		if( componentName.equals( source.getComponentName()))
			return source;
		IJp2pPropertySource<?> result;
		for( IJp2pPropertySource<?> child: source.getChildren() ){
			result = findPropertySource(child, componentName);
			if( result != null )
				return result;
		}
		return null;			
	}

	/**
	 * Find the first ancestor the given directive, starting from the given source
	 * @param source
	 * @param componentName
	 * @return
	 */
	public static IJp2pPropertySource<?> findPropertySource( IJp2pPropertySource<?> source, IJp2pDirectives id, String value ){
		if(( source == null ) || ( id == null ) || ( Utils.isNull( value )))
			return null;
		String directive = source.getDirective(id);
		if( value.equals(directive))
			return source;
		return findPropertySource(source.getParent(), id, value);
	}

	/**
	 * Find the first ancestor the given directive, starting from the given source
	 * @param source
	 * @param componentName
	 * @return
	 */
	public static IJp2pPropertySource<?> findPropertySource( IJp2pPropertySource<?> source, IJp2pDirectives id ){
		if(( source == null ) || ( id == null ))
			return null;
		String directive = source.getDirective(id);
		if( !Utils.isNull( directive))
			return source;
		return findPropertySource( source.getParent(), id );
	}

	/**
	 * Get a boolean value for the given directive
	 * @param source
	 * @param id
	 * @return
	 */
	public static boolean getBoolean( IJp2pPropertySource<?> source, IJp2pDirectives id ){
		String directive = source.getDirective(id);
		if( Utils.isNull( directive ))
			return false;
		return Boolean.parseBoolean( directive);
	}
	
	/**
	 * Get the context of the given property source
	 * @param source
	 * @return
	 */
	public static Contexts getContext( IJp2pPropertySource<?> source ){
		String value = source.getDirective( Directives.CONTEXT );
		if( Utils.isNull( value ))
			return Contexts.JXTA;
		return Contexts.valueOf( StringStyler.styleToEnum( value ));
	}
	
	/**
	 * Get the extended property iterator. This iterator adds the category before the key:
	 *   category.key
	 * @return
	 */
	public static Iterator<IJp2pProperties> getExtendedIterator( IJp2pPropertySource<IJp2pProperties> source) {
		Collection<IJp2pProperties> keys = new ArrayList<IJp2pProperties>();
		Iterator<IJp2pProperties> iterator = source.propertyIterator();
		while( iterator.hasNext()){
			IJp2pProperties prop = iterator.next();
			ManagedProperty<?,?> mp = source.getManagedProperty(prop);
			keys.add( new StringProperty( mp.getCategory() + "." + mp.getKey() ));
		}
		Iterator<IJp2pDirectives> diriterator = source.directiveIterator();
		while( iterator.hasNext()){
			IJp2pDirectives prop = diriterator.next();
			keys.add( new StringProperty( S_DIRECTIVES + "." + prop.toString() ));
		}
		return keys.iterator();
	}

	/**
	 * Get the extended property iterator. This iterator adds the category before the key:
	 *   category.key
	 * @return
	 */
	public static Object getExtendedProperty( IJp2pPropertySource<IJp2pProperties> source, IJp2pProperties key) {
		String[] split = key.toString().split("[.]");
		String id;
		switch( split.length ){
		case 0:
		case 1:
			return source.getProperty(key);
		case 2:
			id = StringStyler.styleToEnum( split[1]);
			if( split[0].equals(S_DIRECTIVES ))
				return source.getDirective( new StringDirective( id ));
			else
				return source.getProperty( new StringProperty( id ));
		default:
			id = StringStyler.styleToEnum( split[ split.length - 1]);
			return source.getProperty( new StringProperty( id ));
		}
	}
	
	/**
	 * Find the first value that was specifically entered for the given directive along the 
	 * ancestors, or null if none was found
	 * @param directive
	 * @return
	 */
	public static String findFirstAncestorDirective( IJp2pPropertySource<?> current, IJp2pDirectives directive ){
		String value = current.getDirective(directive);
		if( !Utils.isNull( value ))
			return value;
		if( current.getParent() == null )
			return null;
		return findFirstAncestorDirective(current.getParent(), directive );
	}
}