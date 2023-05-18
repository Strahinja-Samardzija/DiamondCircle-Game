package model.results;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import logging.LoggerRegistration;
import model.FiguresWalk;
import model.PlayersHand;
import model.FiguresWalk.State;
import model.figures.AbstractFigure;
import model.player.Player;
import properties.PropertiesSingleton;
import property_keys_and_defaults.PropertyKeys;

public class ResultsSaving {

	{
		try {
			new LoggerRegistration<>(ResultsSaving.class).register("Results.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private static final String LOST_RESULTS_PATH = "." + File.separator + "log" + File.separator + "lost_results.txt";
	private final PlayersHand[] hands;

	public ResultsSaving(PlayersHand[] hands) {
		this.hands = hands;
	}

	public void save() {
		DateFormat dateFormat = new SimpleDateFormat("yy_MMM_dd_EEE_HH_mm_ss");
		String currentTime = dateFormat.format(new Date());

		String resultsDir = PropertiesSingleton.getInstance().getProperties()
				.getProperty(PropertyKeys.RESULTS_DIR_KEY);
		if (resultsDir == null) {
			Logger.getLogger(ResultsSaving.class.getName()).log(Level.SEVERE,
					"Could not load \"" + PropertyKeys.RESULTS_DIR_KEY + "\" property's value.\n"
							+ "The application will now shutdown and the result will be written to "
							+ "\"src/log/lost_results.txt\" if possible.\n");
			writeToFile(currentTime, LOST_RESULTS_PATH);
			System.exit(1);
		}

		if (!new File(resultsDir).exists()) {
			new File(resultsDir).mkdirs();
		}
		writeToFile(currentTime, resultsDir + "IGRA_" + currentTime + ".txt");
	}

	private void writeToFile(String currentTime, String path) {
		try (PrintWriter file = new PrintWriter(path, StandardCharsets.UTF_8)) {
			for (PlayersHand player : hands) {
				file.print(createRecordForPlayer(player));
			}
		} catch (IOException e) {
			Logger.getLogger(ResultsSaving.class.getName()).log(Level.SEVERE, () -> "Could not save game's results.\n"
					+ "Current time: " + currentTime + "\n" + e.fillInStackTrace());
		}
	}

	private String createRecordForPlayer(PlayersHand hand) {
		StringBuilder recordBuilder = new StringBuilder(1700);

		Player player = hand.getPlayer();
		recordBuilder.append("Player ").append(player.getId()).append(" - ").append(player.getUsername()).append("\n");

		for (FiguresWalk walk : hand.getWalks()) {
			AbstractFigure figure = walk.getFigure();
			recordBuilder.append("\t").append("Figura ").append(figure.getId()).append("(").append(figure.getType())
					.append(", ").append(figure.getColor()).append(") - pređeni put (").append(walk.getTrace())
					.append(") - stigla do cilja (").append(walk.getState() == State.FINISHED ? "da" : "ne")
					.append(") - vrijeme kretanja (").append(TimeUnit.NANOSECONDS.toSeconds(walk.getElapsedTimeInNanoseconds())).append("s")
					.append(")\n");
		}
		return recordBuilder.toString();
	}

}
