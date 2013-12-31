package net.osgi.jp2p.utils;

import java.util.Comparator;

public class SimpleComparator<T extends Object> implements Comparator<T> {

	@Override
	public int compare(Object arg0, Object arg1) {
		if(( arg0 == null ) && ( arg1 == null ))
			return 0;
		if(( arg0 != null ) && ( arg1 == null ))
			return 1;
		if(( arg0 == null ) && ( arg1 != null ))
			return 1;
		return arg0.toString().compareTo(arg1.toString());
	}

}
