package net.osgi.jxse.service.peergroup;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.jxta.peergroup.PeerGroup;
import net.osgi.jxse.service.core.AbstractJxtaService;
import net.osgi.jxse.service.peergroup.IPeerGroupProperties.PeerGroupProperties;

public class PeerGroupService extends AbstractJxtaService<PeerGroup>{

	public static final String S_PEERGROUP = "Jxta PeerGroup";

	public PeerGroupService(PeerGroup component ) {
		super( component );
	}

	@Override
	protected void fillDefaultValues() {
	}

	public void putProperty( PeerGroupProperties key, Object value ){
		if( value == null )
			return;
	}

	protected void putProperty( PeerGroupProperties key, Object value, boolean skipFilled ){
		if( value == null )
			return;
	}

	public Object getProperty( PeerGroupProperties key ){
		return super.getProperty(key);
	}
	
	@Override
	public Iterator<?> iterator() {
		List<PeerGroupProperties> set = Arrays.asList(PeerGroupProperties.values());
		return set.iterator();
	}

	@Override
	protected void deactivate() {
		// TODO Auto-generated method stub
		
	}
}