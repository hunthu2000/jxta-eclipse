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
package net.osgi.jxse.service;

import org.eclipselabs.osgi.ds.broker.service.AbstractPalaver;
import org.eclipselabs.osgi.ds.broker.service.AbstractPetitioner;
import org.eclipselabs.osgi.ds.broker.service.IParlezListener.Notifications;
import org.eclipselabs.osgi.ds.broker.service.ParlezEvent;

import net.osgi.jxse.utils.StringStyler;

public class MessageBoxPetitioner extends AbstractPetitioner<String, String[], String> {

	public enum MessageTypes{
		INFO,
		QUESTION,
		WARNING,
		ERROR;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}	
	}

	private static MessageBoxPetitioner attendee = new MessageBoxPetitioner();
	
	private String answer;
	
	private MessageBoxPetitioner() {
		super( new MessagePalaver());
		this.answer = null;
	}
	
	public static MessageBoxPetitioner getInstance(){
		return attendee;
	}
	
	public void petition( MessageTypes type, String name, String message ){
		String[] petition = new String[3];
		petition[0] = type.toString();
		petition[1] = name;
		petition[2] = message;
		super.petition(petition);
	}

	public String getAnswer() {
		return answer;
	}

	@Override
	protected void onDataReceived(ParlezEvent<String> event) {
		if( event.getNotification().equals( Notifications.DATA_RECEIVED ))
			this.answer = event.getData();
		super.onDataReceived(event);
	}
}

class MessagePalaver extends AbstractPalaver<String>{

	protected MessagePalaver() {
		super("JXTAMessage", false);
	}

	@Override
	public String giveToken() {
		return "messageBoxToken";
	}

	@Override
	public boolean confirm(Object token) {
		return ( token instanceof String );
	}

	@Override
	public void setClaimAttention(boolean choice) {
		super.setClaimAttention(choice);
	}
}