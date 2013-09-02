package net.osgi.jxse.service.comparator;

import java.util.Comparator;
import java.util.Date;

import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.utils.JxseModuleComparator;


public class JxtaServiceComparator<T extends Object> implements
		Comparator<T> {

	@Override
	public int compare(T arg0, T arg1) {
		if(( arg0 == null ) && ( arg1 == null ))
				return 0;
		if( arg0 == null )
			return -1;
		if( arg1 == null )
			return 1;
	
		int compare =  getIndex( arg0 ) - getIndex( arg1 );
		if( compare != 0 )
			return compare;
		IJxseComponent<?> node1 = (net.osgi.jxse.component.IJxseComponent<?>)arg0;
		IJxseComponent<?> node2 = (net.osgi.jxse.component.IJxseComponent<?>)arg1;
		return this.compareDate( node1.getCreateDate(), node2.getCreateDate() );
	}

	/**
	 * Create an index for the various modules
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected int getIndex( Object obj  ){
		if( obj == null )
			return -1;
		int index = 0;
		if( obj instanceof IJxseServiceContext )
			return index;
		index++;
		if(!( obj instanceof IJxseComponent<?>)){
			index = JxseModuleComparator.getIndex(obj);
			return index;
		}
		IJxseComponent<Object> comp = (IJxseComponent<Object> )obj;
		return JxseModuleComparator.getIndex(comp.getModule());
	}
	
	private int compareDate( Date date1, Date date2 ){
		if(( date1 == null ) && ( date2 == null ))
			return 0;
		if( date1 == null )
			return -1;
		if( date2 == null )
			return 1;
		return date1.compareTo( date2 );

	}

}
