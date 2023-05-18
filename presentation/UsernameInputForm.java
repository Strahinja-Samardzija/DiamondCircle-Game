package presentation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class UsernameInputForm extends HBox {

	private final int playerId;
	private StringProperty username = new SimpleStringProperty();

	public UsernameInputForm(int id) {
		playerId = id;

		Label usernameLabel = new Label("Igrač " + id + ":");
		TextField usernameField = new TextField();
		usernameField.setPromptText("Unesite ime");
		username.bind(usernameField.textProperty());

		super.setSpacing(15);
		super.getChildren().addAll(usernameLabel, usernameField);
	}

	public int getPlayerId() {
		return playerId;
	}

	public String getUsername() {
		return username.get();
	}

	public StringProperty getUsernameProperty() {
		return username;
	}

	public void setUsername(String username) {
		this.username.set(username);
	}
}
