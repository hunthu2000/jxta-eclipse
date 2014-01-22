package net.jp2p.container.validator;

public class RangeValidator<T> extends ClassValidator<T,Integer> {

	private static final String S_ERR_INVALID_RANGE = "The value is not within the range: ";

	private int min, max;
	
	public RangeValidator( T id, int max ) {
		this(id, 0, max, false);
	}

	public RangeValidator( T id, int min, int max, boolean nullable ) {
		super(id, Integer.class, nullable );
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean validate( Integer value) {
		if( !super.validate(value) )
			return false;
		String msg = S_ERR_INVALID_RANGE + super.getId() + ": {" + value + "x> [" + this.min + ", " + this.max + "]}";
		return super.validate(value, ( value >= this.min ) && ( value <= this.max ), msg );
	}

	@Override
	public String getMessage() {
		return null;
	}
}
