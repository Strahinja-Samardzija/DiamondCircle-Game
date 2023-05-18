package controller.scene_controllers.main_scene_controller;

import static presentation.FigureVector.FIGURE_SIZE;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javafx.scene.image.Image;
import model.figures.AbstractFigure;
import model.figures.IFast;
import model.figures.ILevitating;

public class Images {

    Image getImage(StringBuilder pattern, double width, double height) throws IOException {
    	try (BufferedInputStream bf = new BufferedInputStream(new FileInputStream("./images/" + pattern.toString()))) {
    		return new Image(bf, width, height, true, true);
    	}
    }

    Image getImage(AbstractFigure figure) throws IOException {
    	StringBuilder pattern = new StringBuilder(20);
    
    	String kind = "";
    	if (figure instanceof IFast) {
    		kind = "fast_";
    	} else if (figure instanceof ILevitating) {
    		kind = "winged_";
    	}
    	pattern.append(kind).append("" + figure.getColor() + ".png");
    	return getImage(pattern, FIGURE_SIZE, FIGURE_SIZE);
    }
    
}
