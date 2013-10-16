package net.osgi.jxse.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.osgi.jxse.network.NetworkConfigurationPropertySource.NetworkConfiguratorProperties;
import net.osgi.jxse.utils.StringStyler;

public class PartialPropertySource<T extends Enum<T>, U extends IJxseDirectives> extends AbstractJxseWritePropertySource<T, U> 
implements  IJxseWritePropertySource<T, U>{

	private String cat;
	
	public PartialPropertySource(String cat, IJxsePropertySource<NetworkConfiguratorProperties, IJxseDirectives> source) {
		super(source);
		this.cat = cat;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getIdFromString(String key) {
		String id = StringStyler.styleToEnum( cat + "." + key.toLowerCase() );
		return (T) super.getParent().getIdFromString( id );
	}

	@Override
	public String getComponentName() {
		return cat;
	}

	@Override
	public Iterator<T> propertyIterator() {
		Iterator<T> iterator  = super.propertyIterator();
		Collection<T> ids = new ArrayList<T>();
		while( iterator.hasNext() ){
			T id = iterator.next();
			if( id.name().startsWith(cat))
				ids.add(id);
		}
		return ids.iterator();
	}

	@Override
	public boolean validate(T id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}
}
