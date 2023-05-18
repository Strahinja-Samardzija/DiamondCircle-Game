package model.results;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import logging.LoggerRegistration;

public class ResultsReading {

	{
		try {
			new LoggerRegistration<>(ResultsReading.class).register("Results.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public String readResult(String path) {
		StringBuilder result = new StringBuilder(1700);
		try {
			List<String> lines = Files.readAllLines(Path.of(path));
			for (String line : lines) {
				result.append(line);
			}
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).severe(e.fillInStackTrace().toString());
		}
		
		return result.toString();
	}

}
