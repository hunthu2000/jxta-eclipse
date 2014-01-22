package net.jp2p.container.properties;

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ValidationException( String msg ) {
		super( msg);
	}

	public ValidationException( IJp2pValidator<?,?> arg0 ) {
		super(arg0.getMessage());
	}
}
