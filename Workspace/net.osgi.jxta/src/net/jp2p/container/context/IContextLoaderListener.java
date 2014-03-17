package net.jp2p.container.context;

public interface IContextLoaderListener {

	public void notifyRegisterContext( ContextLoaderEvent event );

	public void notifyUnregisterContext( ContextLoaderEvent event );

}
