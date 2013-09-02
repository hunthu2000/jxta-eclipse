package net.osgi.jxse.service.utils;

import net.osgi.jxse.component.IJxseComponent;
import net.osgi.jxse.context.IJxseServiceContext;

public class Utils
{

	/**
	 * Get the label of a JXTA service compoent
	 * @param component
	 * @return
	 */
	public static String getLabel( IJxseComponent<?> component) {
		if( component instanceof IJxseServiceContext ){
			IJxseServiceContext<?> container = (net.osgi.jxse.context.IJxseServiceContext<?> )component;
			return container.getIdentifier();			
		}
		if( component.getModule() == null )
			return component.getClass().getSimpleName();
		return component.getModule().getClass().getSimpleName();
	}
}
