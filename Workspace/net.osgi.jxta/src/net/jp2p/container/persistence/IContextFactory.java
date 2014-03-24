package net.jp2p.container.persistence;

import net.jp2p.container.context.ContextLoader;

public interface IContextFactory {

	public abstract void setLoader(ContextLoader loader);

}