package net.jp2p.container.filter;

public abstract class AbstractFilter<T extends Object>
		implements IFilter<T> {

	private T reference;
	private boolean accept;
	
	protected AbstractFilter( T reference ) {
		this.reference = reference;
	}

	protected T getReference() {
		return reference;
	}

	/**
	 * Provide the conditions on which the filter will accept the event
	 * @param event
	 * @return
	 */
	protected abstract boolean onAccept( T object );
	
	@Override
	public boolean accept( T object ) {
		this.accept = this.onAccept( object );
		return accept;
	}

	@Override
	public boolean hasAccepted() {
		return accept;
	}
}
