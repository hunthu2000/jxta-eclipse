package net.osgi.jp2p.service.osgi;

import java.net.URL;
import java.util.Scanner;

import net.osgi.jp2p.factory.IComponentFactory;
import net.osgi.jp2p.service.core.Jp2pDSComponent;
import net.osgi.jp2p.utils.Utils;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractPetitioner;

public class Jp2pFactoryPetitioner extends AbstractPetitioner<String, String, IComponentFactory<?>>
{
	private static Jp2pFactoryPetitioner attendee = new Jp2pFactoryPetitioner();
	
	private Jp2pFactoryPetitioner() {
		super( new ResourcePalaver());
	}
	
	public static Jp2pFactoryPetitioner getInstance(){
		return attendee;
	}

	/**
	 * Get the factory, or null if it isn't available
	 * @param componentName
	 * @return
	 */
	public IComponentFactory<?> getFactory( String componentName ) {
		for( IComponentFactory<?> factory: super.getCollection() )
			if( factory.getComponentName().equals( componentName ))
				return factory;
		return null;
	}
}

/**
 * The palaver contains the conditions for attendees to create an assembly. In this case, the attendees must
 * pass a string identifier (the package id) and provide a token that is equal
 * @author Kees
 *
 */
class ResourcePalaver extends AbstractPalaver<String>{

	private static final String S_JP2P_INF = "/JP2P-INF/token.txt";
	
	private String providedToken;

	protected ResourcePalaver() {
		super( getProvidedInfo()[0]);
		this.providedToken = getProvidedInfo()[1];
	}

	private static final String[] getProvidedInfo(){
		Class<?> clss = ResourcePalaver.class;
		String[] info = { Jp2pDSComponent.S_IJXSE_CONTAINER_PACKAGE_ID, Jp2pDSComponent.S_IJXSE_TOKEN} ;
		URL url = clss.getResource(S_JP2P_INF );
		if( url == null )
			return info;
		Scanner scanner = null;
		try{
			scanner = new Scanner( clss.getResourceAsStream( S_JP2P_INF ));
			String str = scanner.nextLine();
			if( !Utils.isNull(str))
				info[0] = str;
			str = scanner.nextLine();
			if( !Utils.isNull(str))
				info[1] = str;
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			if( scanner != null )
				scanner.close();
		}
		return null;
		
	}
	
	@Override
	public String giveToken() {
		if( this.providedToken == null )
			return  Jp2pDSComponent.S_IJXSE_TOKEN;
		return this.providedToken;	
	}

	@Override
	public boolean confirm(Object token) {
		if( token == null )
			return false;
		boolean retval = token.equals( Jp2pDSComponent.S_IJXSE_TOKEN ); 
		if( retval )
			return ( retval );
		return token.equals(this.providedToken );
	}	
}