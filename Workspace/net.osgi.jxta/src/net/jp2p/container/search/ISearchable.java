package net.jp2p.container.search;

import net.jp2p.container.filter.IFilter;
import net.jp2p.container.utils.StringStyler;

public interface ISearchable<T,U extends Object> {

	/**
	 * The properties supported by the container
	 * @author Kees
	 *
	 */
	public enum SearchScope{
		CONTAINER,
		ANCESTORS,
		SIBLINGS,
		CHILDREN,
		DESCENDANTS;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	/**
	 * Find the required objects that are accepted by the filter. if 'include' is true,
	 * the references is included in the search, otherwise it is ignored
	 * @param scope
	 * @param root
	 * @param include
	 * @return
	 */
	T[] find( IFilter<T> filter, U reference, boolean include );
}
