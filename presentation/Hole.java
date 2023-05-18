package presentation;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logging.LoggerRegistration;
import properties.PropertiesSingleton;
import property_keys_and_defaults.PropertyKeys;

public class Hole extends ImageView {

	static {
		registerLogger();
	}

	private static void registerLogger() {
		try {
			new LoggerRegistration<>(Hole.class).register("Images.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public static final double HOLE_SIZE = 50.0;
	private static Image img;

	static {
		String imagesDir = PropertiesSingleton.getInstance().getProperties().getProperty(PropertyKeys.IMAGES_DIR_KEY);
		if (imagesDir == null){
			throw new AssertionError("Path to images is missing.");
		}
		try (BufferedInputStream file = new BufferedInputStream(new FileInputStream("./images/hole.png"))) {
			img = new Image(file, HOLE_SIZE, HOLE_SIZE, false, false);
		} catch (Exception e) {
			Logger.getLogger(Hole.class.getName()).severe(e.fillInStackTrace().toString());
		}
	}

	public Hole() throws AssertionError {
		super(img);
	}

}
