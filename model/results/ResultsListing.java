package model.results;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import logging.LoggerRegistration;
import properties.PropertiesSingleton;
import property_keys_and_defaults.PropertyKeys;

public class ResultsListing {
	{
		try {
			new LoggerRegistration<>(ResultsListing.class).register("Results.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private class DirectoryFilter implements FilenameFilter {
		private Pattern pattern;

		public DirectoryFilter(String regex) {
			pattern = Pattern.compile(regex);
		}

		public boolean accept(File dir, String name) {
			return pattern.matcher(name).matches();
		}
	}

	public String[] list() {
		return list(".*");
	}

	public String[] list(String regex) {
		String pathString = PropertiesSingleton.getInstance().getProperties().getProperty(PropertyKeys.RESULTS_DIR_KEY);

		if (pathString == null) {
			Logger.getLogger(getClass().getName()).severe("Could not read results path.");
		}
		
		File path = new File(pathString);
		return path.list(new DirectoryFilter(regex));
	}

	public int recordedGamesCount(){
		return list().length;
	}

}