package net.jp2p.container.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import net.jp2p.container.Jp2pContainerPropertySource;
import net.jp2p.container.properties.AbstractJp2pPropertySource;
import net.jp2p.container.properties.IJp2pProperties;
import net.jp2p.container.properties.IJp2pPropertySource;
import net.jp2p.container.properties.IJp2pWritePropertySource;
import net.jp2p.container.utils.IOUtils;
import net.jp2p.container.utils.ProjectFolderUtils;

public class PersistedProperties extends
		AbstractPersistedProperty<String> {

	public static final String S_PROPERTIES = "properties.txt";
	
	public PersistedProperties(IJp2pWritePropertySource<IJp2pProperties> source) {
		super(source);
	}

	
	@Override
	public String getProperty(IJp2pProperties id) {
		Properties properties = loadProperties( super.getSource() );
		return (String) properties.getProperty( id.toString() );
	}

	@Override
	public boolean setProperty(IJp2pProperties id, String value) {
		IJp2pWritePropertySource<IJp2pProperties> source = (IJp2pWritePropertySource<IJp2pProperties>) super.getSource();
		Properties properties = loadProperties( super.getSource() );
		properties.setProperty(id.toString(), value);
		return saveProperties(source, properties);
	}

	/**
	 * Load the properties from a file location
	 * @param source
	 * @param properties
	 * @throws FileNotFoundException
	 */
	protected static Properties loadProperties( IJp2pPropertySource<IJp2pProperties> source ){
		String bundle_id = (String) AbstractJp2pPropertySource.getBundleId( source );
		String str = ProjectFolderUtils.getParsedUserDir( Jp2pContainerPropertySource.DEF_HOME_FOLDER + File.separator + S_PROPERTIES, bundle_id ).getPath();
		File file = new File( str );
		FileReader reader;
		Properties properties = new Properties();
		try {
			reader = new FileReader( file );
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return properties;
		}
		try {
			properties.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			IOUtils.closeReader( reader );
		}
		return properties;
	}

	/**
	 * Load the properties from a file location
	 * @param source
	 * @param properties
	 * @throws FileNotFoundException
	 */
	protected static boolean saveProperties( IJp2pPropertySource<IJp2pProperties> source, Properties properties ){
		String bundle_id = (String) AbstractJp2pPropertySource.getBundleId( source );
		String str = ProjectFolderUtils.getParsedUserDir( Jp2pContainerPropertySource.DEF_HOME_FOLDER + File.separator + S_PROPERTIES, bundle_id ).getPath();
		File file = new File( str );
		FileWriter writer = null;
		try {
			writer = new FileWriter( file );
			properties.store(writer, Calendar.getInstance().getTime().toString());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			IOUtils.closeWriter( writer );
		}
		return false;
	}

}
