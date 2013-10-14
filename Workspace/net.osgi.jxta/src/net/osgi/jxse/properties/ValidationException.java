package net.osgi.jxse.properties;

public class ValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ValidationException( String msg ) {
		super( msg);
	}

	public ValidationException( IJxseValidator<?,?> arg0 ) {
		super(arg0.getMessage());
	}
}
