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
package net.osgi.jp2p.service.comparator;

import java.util.Comparator;
import java.util.Date;

import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.context.IJxseServiceContainer;
import net.osgi.jp2p.utils.JxseModuleComparator;


public class Jp2pServiceComparator<T extends Object> implements
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
		IJp2pComponent<?> node1 = (net.osgi.jp2p.component.IJp2pComponent<?>)arg0;
		IJp2pComponent<?> node2 = (net.osgi.jp2p.component.IJp2pComponent<?>)arg1;
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
		if( obj instanceof IJxseServiceContainer )
			return index;
		index++;
		if(!( obj instanceof IJp2pComponent<?>)){
			index = JxseModuleComparator.getIndex(obj);
			return index;
		}
		IJp2pComponent<Object> comp = (IJp2pComponent<Object> )obj;
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
