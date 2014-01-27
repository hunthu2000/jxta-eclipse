package net.jp2p.container.properties;

public class SimplePropertyConvertor extends AbstractPropertyConvertor<String, Object> {

	public SimplePropertyConvertor( IJp2pWritePropertySource<IJp2pProperties> source ) {
		super( source );
	}

	@Override
	public String convertFrom(IJp2pProperties id) {
		Object value = super.getSource().getProperty(id);
		if( value == null )
			return null;
		return value.toString();
	}

	@Override
	public Object convertTo(IJp2pProperties id,String value) {
		return null;
	}
}
