package net.jp2p.container.builder;

import net.jp2p.container.ContainerFactory;

public interface IFactoryBuilder {

	public static String S_DEFAULT_FOLDER = "/JP2P-INF";
	public static String S_DEFAULT_LOCATION = S_DEFAULT_FOLDER + "/jp2p-1.0.0.xml";
	public static String S_SCHEMA_LOCATION =  S_DEFAULT_FOLDER + "/jp2p-schema.xsd";

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#addListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	public abstract void addListener(ICompositeBuilderListener<?> listener);

	/* (non-Javadoc)
	 * @see net.osgi.jxta.factory.ICompositeFactory#removeListener(net.osgi.jxta.factory.ICompositeFactoryListener)
	 */
	public abstract void removeListener(ICompositeBuilderListener<?> listener);

	public abstract ContainerFactory build();

	public abstract boolean isCompleted();

}