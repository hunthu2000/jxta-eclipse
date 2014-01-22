package net.jp2p.container.factory;

public class FactoryException extends RuntimeException {
	private static final long serialVersionUID = -8597836283568092235L;

	public FactoryException() {
	}

	public FactoryException(String arg0) {
		super(arg0);
	}

	public FactoryException(Throwable arg0) {
		super(arg0);
	}

	public FactoryException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public FactoryException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
