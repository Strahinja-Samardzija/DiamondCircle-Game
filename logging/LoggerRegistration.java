package logging;

import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.FileHandler;

import properties.PropertiesSingleton;
import property_keys_and_defaults.PropertyKeys;

public class LoggerRegistration<T> {

	private Class<T> typeParameterClass;

	public LoggerRegistration(Class<T> typeParameterClass) {
		this.typeParameterClass = typeParameterClass;
	}

	public void register(String logFilename) throws SecurityException, IOException {
		String logDir = PropertiesSingleton.getInstance().getProperties().getProperty(PropertyKeys.LOG_DIR_KEY);
		FileHandler logHandler;
		if (logDir == null) {
			logHandler = new FileHandler(PropertyKeys.LOG_DIR_DEFAULT + logFilename);
		} else {
			logHandler = new FileHandler(logDir + logFilename);
		}

		Logger logger = Logger.getLogger(typeParameterClass.getName());
		logger.addHandler(logHandler);
		logger.setUseParentHandlers(false);
	}
}
