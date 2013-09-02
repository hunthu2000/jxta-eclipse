package net.osgi.jxse.service.core;

import net.osgi.jxse.service.IContextObserver;

public interface IBuilderContext<T extends Object> {

	public abstract IContextObserver<T> getObserver();

	public abstract void setObserver(IContextObserver<T> observer);

}