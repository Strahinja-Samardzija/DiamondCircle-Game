package presentation;

import java.util.Objects;
import exceptions.TrackNotSetException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FigureVector extends ImageView {

	public static final double FIGURE_SIZE = 30.0;

	private final int figuresId;
	private volatile boolean isDead = false;
	private IntegerProperty tilesFromStart = new SimpleIntegerProperty(0);
	private Track track;

	@Override
	public int hashCode() {
		return Objects.hash(figuresId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FigureVector other = (FigureVector) obj;
		return figuresId == other.figuresId;
	}

	public FigureVector(Image img, int id) {
		super(img);
		figuresId = id;
	}

	public int getFiguresId() {
		return figuresId;
	}

	public void stepForward() {
		if (isDead)
			return;
		tilesFromStart.set(tilesFromStart.get() + 1);
	}

	public boolean isDead() {
		return isDead;
	}

	public void die() {
		this.isDead = true;
	}

	public int getTilesFromStart() {
		return tilesFromStart.get();
	}

	public IntegerProperty tilesFromStartProperty() {
		return tilesFromStart;
	}

    public Track getTrack() throws TrackNotSetException {
		if (track == null){
			throw new TrackNotSetException();
		}
        return track;
    }

    public void setTrack(Track track) {
		this.track = track;
    }

}
