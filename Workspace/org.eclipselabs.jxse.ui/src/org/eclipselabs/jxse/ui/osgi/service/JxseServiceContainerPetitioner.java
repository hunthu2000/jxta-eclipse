package org.eclipselabs.jxse.ui.osgi.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import net.jxta.document.Advertisement;
import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.component.IJxseComponentNode;
import net.osgi.jxse.context.IJxseServiceContext;
import net.osgi.jxse.service.IServiceChangedListener;
import net.osgi.jxse.service.ServiceChangedEvent;
import net.osgi.jxse.service.ServiceEventDispatcher;
import net.osgi.jxse.service.comparator.JxtaServiceComparator;
import net.osgi.jxse.utils.Utils;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractPetitioner;
import org.eclipselabs.osgi.ds.broker.service.IParlezListener.Notifications;
import org.eclipselabs.osgi.ds.broker.service.ParlezEvent;

public class JxseServiceContainerPetitioner extends AbstractPetitioner<String, String, IJxseServiceContext<?>>
	implements IJxseComponentNode<Collection<IJxseServiceContext<?>>>, IServiceChangedListener
{
	public static final String S_JXTA = "Jxta";

	private static JxseServiceContainerPetitioner attendee = new JxseServiceContainerPetitioner();
	
	private List<IJxseComponent<?>> children;

	private ServiceEventDispatcher dispatcher = ServiceEventDispatcher.getInstance();
	
	private Date date;
	
	private JxseServiceContainerPetitioner() {
		super( new Palaver());
		children = new ArrayList<IJxseComponent<?>>();
		this.date = Calendar.getInstance().getTime();
	}
	
	public static JxseServiceContainerPetitioner getInstance(){
		return attendee;
	}

	public IJxseServiceContext<?> getJxtaContainer( String identifier ) {
		for( IJxseServiceContext<?> container: super.getCollection() )
			if( container.getIdentifier().equals( identifier ))
				return container;
		return null;
	}

	@Override
	protected void onDataReceived( ParlezEvent<IJxseServiceContext<?>> event ) {
		  super.onDataReceived( event );
		  this.addChild( event.getData());
		  System.out.println("Container added");
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
	public IJxseComponentNode<?> getParent() {
		return null;
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
	public void putProperty(Object key, Object value) {
		// TODO Auto-generated method stub	
	}

	@Override
	public Collection<IJxseServiceContext<?>> getModule() {
		return super.getCollection();
	}

	@Override
	public Iterator<?> iterator() {
		return null;
	}

	@Override
	public Collection<IJxseComponent<?>> getChildren() {
		return children;
	}

	@Override
	public void notifyServiceChanged(ServiceChangedEvent event) {
		super.notifyChange( new ParlezEvent<Object>( this, Notifications.DATA_UPDATED ));
		
	}

	@Override
	public void addChild(IJxseComponent<?> child) {
		if( children.contains( child ))
			return;
		children.add( child );
		Collections.sort( children, new JxtaServiceComparator<Object>());
		dispatcher.serviceChanged( new ServiceChangedEvent( this, ServiceChange.CHILD_ADDED ));
	}

	@Override
	public void removeChild(IJxseComponent<?> child) {
		  children.remove( child );
		  dispatcher.serviceChanged( new ServiceChangedEvent( this, ServiceChange.CHILD_REMOVED ));
	}

	@Override
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	@Override
	public Advertisement[] getAdvertisements() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Returns true if the component has advertisements
	 * @return
	 */
	@Override
	public boolean hasAdvertisements(){
		return false;
	}

}

/**
 * The palaver contains the conditions for attendees to create an assembly. In this case, the attendees must
 * pass a string identifier (the package id) and provide a token that is equal
 * @author Kees
 *
 */
class Palaver extends AbstractPalaver<String>{

	private static final String S_IJXTACONTAINER_PACKAGE_ID = "org.osgi.jxta.service.ijxtaservicecomponent";
	private static final String S_IJXTA_TOKEN = "org.osgi.jxta.token";

	private static final String S_JXSE_INF = "/JXSE-INF/token.txt";
	
	private String providedToken;

	protected Palaver() {
		super( getProvidedInfo()[0]);
		this.providedToken = getProvidedInfo()[1];
	}

	private static final String[] getProvidedInfo(){
		Class<?> clss = Palaver.class;
		URL url = clss.getResource(S_JXSE_INF );
		if( url == null )
			return null;
		Scanner scanner = null;
		try{
			scanner = new Scanner( clss.getResourceAsStream( S_JXSE_INF ));
			String[] info = {S_IJXTACONTAINER_PACKAGE_ID, S_IJXTA_TOKEN} ;
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
			return S_IJXTA_TOKEN;
		return this.providedToken;	
	}

	@Override
	public boolean confirm(Object token) {
		if( token == null )
			return false;
		boolean retval = token.equals(S_IJXTA_TOKEN ); 
		if( retval )
			return ( retval );
		return token.equals(this.providedToken );
	}	
}