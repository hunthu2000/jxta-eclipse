package net.osgi.jxse.advertisement;

import java.util.Enumeration;

import net.jxta.document.Element;
import net.osgi.jxse.properties.AbstractJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.properties.ManagedProperty;

public class ElementPropertySource<E extends Element<E>> extends AbstractJxsePropertySource<String, IJxseDirectives> {

	private Element<E> root;
	
	public ElementPropertySource(IJxsePropertySource<?, ?> parent, Element<E> root) {
		super(parent);
		this.root = root;
	}

	public ElementPropertySource(String componentName,
			IJxsePropertySource<?, ?> parent, Element<E> root) {
		super(componentName, parent);
		this.root = root;
	}

	@Override
	public String getIdFromString(String key) {
		return key;
	}
	
	@Override
	public ManagedProperty<String, Object> getManagedProperty(String id) {
		return this.getManagedProperty(id, this.root );
	}
	
	/**
	 * Find the element with the given id and convert it to a managed property.
	 * @param id
	 * @param element
	 * @return
	 */
	protected ManagedProperty<String, Object> getManagedProperty( String id, Element<?> element ){
		if( element.getKey().equals(id )){
			return new ManagedProperty<String,Object>( id, element.getValue(), false );
		}
		Enumeration<?> children = element.getChildren();
		ManagedProperty<String,Object> prop;
		while( children.hasMoreElements()){
			prop = getManagedProperty(id, (Element<?>) children.nextElement() );
			if( prop != null  )
				return prop;
		}
		return null;
	}

	@Override
	public boolean validate(String id, Object value) {
		return false;
	}

	@Override
	public int hashCode() {
		return root.hashCode();
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
