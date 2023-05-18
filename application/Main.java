package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import logging.LoggerRegistration;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

	static {
		PrintStream oldErr = System.err;
		try {
			PrintStream newErr = new PrintStream(
					new FileOutputStream("." + File.separator + "log" + File.separator + "system_err.txt"));
			System.setErr(newErr);
		} catch (FileNotFoundException e) {
			System.setErr(oldErr);
			e.printStackTrace();
		}
	}

	{
		try {
			new LoggerRegistration<>(Main.class).register("Main.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			var loader = new FXMLLoader(getClass().getResource("/presentation/StartScene.fxml"));
			Parent pane = loader.load();
			Scene scene = new Scene(pane);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static void restart(Stage mainStage) {
		new Main().start(mainStage);
	}
}
