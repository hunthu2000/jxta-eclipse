/*******************************************************************************
 * Copyright (c) 2013 Condast and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kees Pieters - initial API and implementation
 *******************************************************************************/
package net.osgi.jxse.service.xml;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.osgi.jxse.context.IJxseServiceContext.ContextProperties;
import net.osgi.jxse.properties.AbstractJxsePropertySource;
import net.osgi.jxse.properties.IJxseDirectives;
import net.osgi.jxse.properties.IJxsePropertySource;
import net.osgi.jxse.service.xml.PreferenceStore.Persistence;
import net.osgi.jxse.service.xml.PreferenceStore.SupportedAttributes;
import net.osgi.jxse.utils.ProjectFolderUtils;
import net.osgi.jxse.utils.StringStyler;
import net.osgi.jxse.utils.Utils;

public class JxseXMLPreferences extends AbstractJxsePropertySource<ContextProperties, IJxseDirectives> implements IJxsePropertySource<ContextProperties, IJxseDirectives> {

	public static final String S_XML_ROOT = "JXSE Component Root";
	private PreferenceStore store;
	
	public JxseXMLPreferences( String pluginId, String identifier, PreferenceStore store ) {
		super( pluginId, identifier, S_XML_ROOT );
		this. store = store;
	}

	/**
	 * Returns true is only the identifier is stored as preference
	 * @return
	 */
	boolean containsOnlyIdentifierAtBest(){
		if( store.isEmpty() )
			return false;
		String identifier = getComponentName();
		if(( identifier == null ) || ( identifier.trim().length() == 0 ))
			return false;
		return store.size() <= 2;
	}

	public String getBundleId() {
		return store.getValue(  ContextProperties.BUNDLE_ID.toString() );
	}

	public boolean getRendezVousAutostart() {
		return Boolean.valueOf( store.getValue( ContextProperties.RENDEZVOUZ_AUTOSTART.toString()));
	}

	public ConfigMode getConfigMode() {
		String value = store.getValue( ContextProperties.CONFIG_MODE.toString());
		if( Utils.isNull(value ))
			return null;
		return ConfigMode.valueOf( value );
	}

	public URI getHomeFolder() throws URISyntaxException {
		String str = store.getValue( ContextProperties.HOME_FOLDER.toString() );
		if(( str == null ) || ( str.trim().length() == 0 ))
			return null;
		File file = new File( ProjectFolderUtils.getParsedUserDir( str, this.getBundleId() ));
		return file.toURI();
	}

	public PeerID getPeerID() throws URISyntaxException {
		PersistentAttribute pa = store.getAttribute( ContextProperties.PEER_ID.toString());
		String value = store.getValue( pa.getKey());
		PeerID pgId = null;
		URI uri;
		switch( pa.getPersistence() ){
		case GENERATED:
			if( value == null ){
				  String name = getComponentName();
				  pgId = IDFactory.newPeerID( PeerGroupID.defaultNetPeerGroupID, name.getBytes() );
				  store.setValue( pa.getKey(), pgId.toString() );
			}
			break;
		case PARSED:
			value = pa.getValue();
			uri = new URI( value );
			pgId = (PeerID) IDFactory.fromURI( uri );
			break;
		case PREFERENCES:
			value = pa.getValue();
			uri = new URI( value );
			pgId = (PeerID) IDFactory.fromURI( uri );
			break;
		default:
			return null;
		}	
		return pgId;
	}
	
	void putProperty( Map<SupportedAttributes, String> attributes, String key, String value ){
		if( attributes == null )
			store.addPersistentAttribute( key, value);
		else
			store.addPersistentAttribute( attributes, key, value);
	}

	protected static Map<SupportedAttributes, String> parseAttributes( Attributes attrs ){
		if(( attrs == null ) || ( attrs.getLength() == 0 ))
			return new HashMap<SupportedAttributes, String>();
		Map<SupportedAttributes, String> results = new HashMap<SupportedAttributes, String>();
		for( int i=0; i<attrs.getLength(); i++ ){
			String key = StringStyler.styleToEnum(attrs.getQName(i));
			SupportedAttributes sa = SupportedAttributes.valueOf( key );
			switch( sa ){
			case PERSIST:
				String value = StringStyler.styleToEnum(attrs.getValue(i));
				Persistence.valueOf( value);
				results.put( sa, attrs.getValue(i));
				break;
			default:
				results.put( sa, attrs.getValue(i));
				break;
			}
			
		}
		return results;
	}

	/**
	 * Returns true if the attribute is a persistent property
	 * @param attrs
	 * @return
	 */
	static Persistence isPersistentProperty( Attributes attrs ){
		if(( attrs == null ) || ( attrs.getLength() == 0 ))
			return Persistence.NULL;
		Map<SupportedAttributes, String> sa =  parseAttributes( attrs );
		if( sa.get( SupportedAttributes.PERSIST ) == null )
			return Persistence.NULL;
		return Persistence.valueOf( sa.get( SupportedAttributes.PERSIST ));
	}

	@Override
	public String getComponentName() {
		return store.getValue( ContextProperties.IDENTIFIER.toString() );
	}

	@Override
	public Object getDefault(ContextProperties id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validate(ContextProperties id, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getDefaultDirectives(IJxseDirectives id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextProperties getIdFromString(String key) {
		// TODO Auto-generated method stub
		return null;
	}
}
