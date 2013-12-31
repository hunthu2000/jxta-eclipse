package net.osgi.jp2p.validator;

import net.osgi.jp2p.properties.AbstractJp2pValidator;

public class ClassValidator<T,U> extends AbstractJp2pValidator<T,U> {

	private static final String S_ERR_INVALID_CLASS = "The class is invalid of ";

	private Class<U> clss;
	
	public ClassValidator( T id, Class<U> clss ) {
		this(id, clss, false);
	}

	public ClassValidator( T id, Class<U> clss, boolean nullable ) {
		super(id, nullable );
		this.clss = clss;
	}

	@Override
	public boolean validate(Object value) {
		if( !super.validate(value) )
			return false;
		String msg = S_ERR_INVALID_CLASS + super.getId() + ": {" + value.getClass().getName() + "->" +  clss.getName() + "}";
		return super.validate(value, value.getClass().equals( clss ), msg );
	}

	@Override
	public String getMessage() {
		return null;
	}

}
