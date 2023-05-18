package presentation;

import javafx.scene.control.Label;
import model.player.Player.COLOR;

public class PlayerLabel extends Label {

	public PlayerLabel(String name, COLOR color) {
		setText(name);
		setStyle("-fx-text-fill: \"" + color +"\"");
	}
}
