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
package net.osgi.jxta.seeds;

import java.net.URI;


public class SeedInfo implements ISeedInfo{

	private SeedTypes seedType;
	
	private String url;
	
	private boolean commentedOut;
	
	public SeedInfo() {
		this.commentedOut = false;
	}
	
	public void parse( String key, String value ){
		if( key.startsWith("//") || key.startsWith("/*)")){
			this.commentedOut = true;
			return;
		}
		String[] split = value.split("[,]");
		seedType = SeedTypes.valueOf(split[0].trim());
		url = split[1].trim();
	}

	/**
	 * Returns true id=f the line is commented out
	 * @return
	 */
	public boolean isCommentedOut(){
		return this.commentedOut;
	}
	
	public SeedTypes getSeedType() {
		return seedType;
	}

	public URI getUri() {
		return URI.create( url );
	}
}
