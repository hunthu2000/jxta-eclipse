package org.chaupal.jp2p.ui.osgi.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import net.osgi.jp2p.chaupal.IServiceChangedListener;
import net.osgi.jp2p.chaupal.ServiceChangedEvent;
import net.osgi.jp2p.chaupal.ServiceEventDispatcher;
import net.osgi.jp2p.chaupal.comparator.Jp2pServiceComparator;
import net.osgi.jp2p.chaupal.core.Jp2pDSComponent;
import net.osgi.jp2p.component.ComponentChangedEvent;
import net.osgi.jp2p.component.IComponentChangedListener;
import net.osgi.jp2p.component.IJp2pComponent;
import net.osgi.jp2p.component.IJp2pComponentNode;
import net.osgi.jp2p.container.Jp2pServiceContainer;
import net.osgi.jp2p.properties.IJp2pProperties;
import net.osgi.jp2p.properties.IJp2pPropertySource;
import net.osgi.jp2p.utils.Utils;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractPetitioner;
import org.eclipselabs.osgi.ds.broker.service.IParlezListener.Notifications;
import org.eclipselabs.osgi.ds.broker.service.ParlezEvent;

public class Jp2pServiceContainerPetitioner extends AbstractPetitioner<String, String, Jp2pServiceContainer>
	implements IJp2pComponentNode<Collection<Jp2pServiceContainer>>, IServiceChangedListener
{
	public static final String S_JXSE = "Jxse";

	private static Jp2pServiceContainerPetitioner attendee = new Jp2pServiceContainerPetitioner();
	
	private List<IJp2pComponent<?>> children;

	private ServiceEventDispatcher dispatcher = ServiceEventDispatcher.getInstance();	
	private IComponentChangedListener listener;
	
	private Date date;
	
	private Jp2pServiceContainerPetitioner() {
		super( new ResourcePalaver());
		children = new ArrayList<IJp2pComponent<?>>();
		this.date = Calendar.getInstance().getTime();
		this.listener = new IComponentChangedListener() {
			
			@Override
			public void notifyServiceChanged(ComponentChangedEvent event) {
				dispatcher.serviceChanged( new ServiceChangedEvent( this, ServiceChange.COMPONENT_EVENT ));
			}
		};
	}
	
	public static Jp2pServiceContainerPetitioner getInstance(){
		return attendee;
	}

	
	@Override
	public IJp2pPropertySource<IJp2pProperties> getPropertySource() {
		// TODO Auto-generated method stub
		return null;
	}

	public Jp2pServiceContainer getJxtaContainer( String identifier ) {
		for( Jp2pServiceContainer container: super.getCollection() )
			if( container.getIdentifier().equals( identifier ))
				return container;
		return null;
	}

	@Override
	protected void onDataReceived( ParlezEvent<Jp2pServiceContainer> event ) {
		  super.onDataReceived( event );
		  this.addChild( event.getData());
		  System.out.println("Container added: " + event.getData().getIdentifier( ));
	}

	@Override
	public String getId() {
		return this.getClass().getPackage().getName() + ".petitioner";
	}

	/**
	 * Get the create date
	 */
	public Date getCreateDate(){
		return (Date) this.date;
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public Object getProperty(Object key) {
		return null;
	}

	
	@Override
	public void setMatched( boolean choice ) {
		super.setMatched(choice);
	}

	@Override
	public Collection<Jp2pServiceContainer> getModule() {
		return super.getCollection();
	}

	@Override
	public Collection<IJp2pComponent<?>> getChildren() {
		return children;
	}

	@Override
	public void notifyServiceChanged(ServiceChangedEvent event) {
		super.notifyChange( new ParlezEvent<Object>( this, Notifications.DATA_UPDATED ));
		
	}

	@Override
	public void addChild(IJp2pComponent<?> child) {
		if( children.contains( child ))
			return;
		children.add( child );
		Jp2pServiceContainer container = (Jp2pServiceContainer) child;
		container.getDispatcher().addServiceChangeListener( listener );
		Collections.sort( children, new Jp2pServiceComparator<Object>());
		dispatcher.serviceChanged( new ServiceChangedEvent( this, ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild(IJp2pComponent<?> child) {
		children.remove( child );
		Jp2pServiceContainer container = (Jp2pServiceContainer) child;
		container.getDispatcher().removeServiceChangeListener( listener );
		dispatcher.serviceChanged( new ServiceChangedEvent( this, ServiceChange.CHILD_REMOVED ));
	}

	@Override
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	@Override
	public Iterator<IJp2pProperties> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCategory(Object key) {
		// TODO Auto-generated method stub
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
		String[] info = { Jp2pDSComponent.S_IJP2P_CONTAINER_PACKAGE_ID, Jp2pDSComponent.S_IP2P_TOKEN} ;
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
			return  Jp2pDSComponent.S_IP2P_TOKEN;
		return this.providedToken;	
	}

	@Override
	public boolean confirm(Object token) {
		if( token == null )
			return false;
		boolean retval = token.equals( Jp2pDSComponent.S_IP2P_TOKEN ); 
		if( retval )
			return ( retval );
		return token.equals(this.providedToken );
	}	
}