package properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import property_keys_and_defaults.PropertyKeys;

public class PropertiesSingleton {

	private static final String CONFIG_LOG_PROPERTIES_LOG = "./config/log/properties.log";
	private FileHandler logHandler;

	{
		try {
			logHandler = new FileHandler(CONFIG_LOG_PROPERTIES_LOG);

			Logger logger = Logger.getLogger(PropertiesSingleton.class.getName());
			logger.addHandler(logHandler);
			logger.setUseParentHandlers(false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private static PropertiesSingleton instance = null;
	private final Properties applicationProperties;

	private PropertiesSingleton() {
		// load default properties
		Properties defaultProperties = new Properties();
		try (FileInputStream in = new FileInputStream(PropertyKeys.CONFIG_DEFAULT_CONFIG_PROPERTIES);) {
			defaultProperties.load(in);
		} catch (IOException e) {
			Logger.getLogger(PropertiesSingleton.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
		}

		// create application properties with default
		this.applicationProperties = new Properties(defaultProperties);

		// now load properties
		try (FileInputStream in = new FileInputStream(PropertyKeys.CONFIG_USER_CONFIG_PROPERTIES)) {
			this.applicationProperties.load(in);
		} catch (IOException e) {
			Logger.getLogger(PropertiesSingleton.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}

	public static PropertiesSingleton getInstance() {
		if (instance != null)
			return instance;
		else {
			instance = new PropertiesSingleton();
			return instance;
		}
	}

	public Properties getProperties() {
		return applicationProperties;
	}
}
