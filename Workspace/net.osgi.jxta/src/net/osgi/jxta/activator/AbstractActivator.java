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
/**
 * 
 */
package net.osgi.jxta.activator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author keesp
 *
 */
public abstract class AbstractActivator<T extends Object> implements IActivator
{

	private Status status;
	
	private Collection<IActivatorListener> listeners;

	/**
	 * 
	 */
	protected AbstractActivator()
	{
		this.status = Status.IDLE;
		listeners = new ArrayList<IActivatorListener>();
	}

	/**
	 * 
	 */
	public AbstractActivator( T factory )
	{
		this( factory, false );
	}

	/**
	 * 
	 */
	protected AbstractActivator( T factory, boolean skipAvailable )
	{
		this.status = Status.IDLE;
		listeners = new ArrayList<IActivatorListener>();
		if(!skipAvailable )
		  this.setAvailable( factory );
	}
	public void addActivatorListener( IActivatorListener listener ){
		this.listeners.add( listener );
	}

	public void removeActivatorListener( IActivatorListener listener ){
		this.listeners.remove( listener );
	}

	protected void notifyListeners( Status previous, Status status ){
		for( IActivatorListener listener: this.listeners )
			listener.notifyStatusChanged( new ActivatorEvent( this, previous ));
	}
	
	/* (non-Javadoc)
	 * @see org.condast.concept.context.IActivator#getStatus()
	 */
	@Override
	public Status getStatus()
	{
		return this.status;
	}

	
	protected void setStatus(Status status) {
		Status previous = this.status;
		this.status = status;
		this.notifyListeners(previous, status);
	}

	/**
	 * Set the activator to idel
	 */
	protected void clear(){
		this.setStatus( Status.IDLE );
	}

	/**
	 * Make the activator AVAILABLE
	 */
	protected void setAvailable( T factory ){
		if( this.onSetAvailable( factory ))
			this.setStatus( Status.AVAILABLE );
	}
	protected abstract boolean onSetAvailable( T factory );

	/**
	 * Initialise the activator. If the method returns false, the status remains on initialising
	 */
	protected void initialise(){
		if( this.status != Status.AVAILABLE )
			return;
		this.setStatus( Status.INITIALISING );
		if( this.onInitialising())
			this.setStatus( Status.INITIALISED );
	}
	protected abstract boolean onInitialising();

	/**
	 * Finalise the activator
	 */
	protected void finalise(){
		if(!( this.status == Status.IDLE ))
			this.deactivate();
		this.setStatus( Status.FINALISING );
		this.onFinalising();	
		this.setStatus( Status.FINALISED );
	}
	protected abstract void onFinalising();

	/**
	 * Supportive method gives true if the status is set to IDLE
	 * @return
	*/
	@Override
	public boolean isIdle()
	{
		return ( this.status.equals( Status.IDLE ));		
	}

	/* (non-Javadoc)
	 * @see org.condast.concept.context.IActivator#isAVAILABLE()
	 */
	@Override
	public boolean isAvailable()
	{
		return ( this.status.equals( Status.AVAILABLE ));
	}

	/* (non-Javadoc)
	 * @see org.condast.concept.context.IActivator#isAVAILABLE()
	 */
	@Override
	public boolean isActive()
	{
		return ( this.status.equals( Status.ACTIVE ));
	}

	/**
	 * Start activating. Returns true if the activation is possible, false
	 * if not, for instance when the activator is already started
	 */
	@Override
	public boolean start(){
		if( !this.status.equals( Status.AVAILABLE ) && 
				!this.status.equals( Status.INITIALISED ) && 
				!this.status.equals( Status.PAUSED ))
			return false;
		if( this.status.equals( Status.AVAILABLE ) || 
				this.status.equals( Status.INITIALISED )){
			this.setStatus( Status.ACTIVATING );
		  this.activate();
		}
		this.setStatus( Status.ACTIVE );
		return true;
	}

	/**
	 * Set to PAUSED. Returns true if this is possible, false
	 * if not, for instance when the activator is not started or already PAUSED
	 */
	@Override
	public boolean pause(){
		if( !this.status.equals( Status.ACTIVE) || this.status.equals( Status.PAUSED ))
			return false;
		this.setStatus( Status.PAUSED );
		return true;
	}

	/**
	 * Stop activating
	 */
	@Override
	public void stop(){
		if( this.status.equals( Status.IDLE ))
			return;
		this.setStatus( Status.SHUTTING_DOWN );
		this.deactivate();
	}
	
	/**
	 * Activate the activator
	*/
	protected void activate(){
		this.setStatus( Status.ACTIVE );
	}
	
	/**
	 * Shut the activator down
	 */
	protected abstract void deactivate();

}