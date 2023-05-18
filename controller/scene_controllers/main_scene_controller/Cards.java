package controller.scene_controllers.main_scene_controller;

import java.io.IOException;

import javafx.scene.image.Image;
import model.FiguresWalk;
import model.cards.ICard;
import model.cards.ISpecialCard;
import model.cards.IStepsCard;
import model.cards.StepsCard;
import model.figures.IFast;

public class Cards {

	public void paintCard(MainSceneController mainSceneController, ICard newCard) throws IOException {
		StringBuilder pattern = new StringBuilder(20);
		if (newCard instanceof ISpecialCard) {
			pattern.append("special_card.png");
		} else {
			pattern.append(((StepsCard) newCard).getSteps().toString()).append("_card.png");
		}

		Image image;
		image = mainSceneController.images.getImage(pattern, mainSceneController.cardImageView.getFitWidth(),
				mainSceneController.cardImageView.getFitHeight());

		Runnable task = () -> mainSceneController.cardImageView.setImage(image);
		mainSceneController.waitToUpdateScene(task);
	}

	public void updateCardDescription(MainSceneController mainSceneController, ICard newCard, FiguresWalk walk) {
		StringBuilder description = new StringBuilder(60);
		if (newCard instanceof ISpecialCard) {
			description.append("Otvaraju se rupe. Samo figure koje lebde ne mogu pasti u rupu.");
		} else {
			description.append("Na potezu je igrač ").append(walk.getFigure().getColor().toString());
			description.append("\nFigura ").append(walk.getId()).append(" prelazi ");
			description.append(((IStepsCard) newCard).getSteps().intValue).append(" polja na osnovu izvučene karte.");
			if (walk.getFigure() instanceof IFast) {
				description.append("\nFigura je brza pa prelazi dodatnih ")
						.append(((IStepsCard) newCard).getSteps().intValue).append(" polja.");
			}
			if (walk.getFigure().getCollectedDiamondsCount() > 0) {
				description.append("\nNa osnovu skupljenih dijamanata prelazi još ")
						.append(walk.getFigure().getCollectedDiamondsCount()).append(" polja.");
			}
		}
		Runnable task = () -> mainSceneController.cardDescriptionContentLabel.setText(description.toString());
		mainSceneController.waitToUpdateScene(task);
	}
}
