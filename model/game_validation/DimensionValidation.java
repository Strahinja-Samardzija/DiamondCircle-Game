package model.game_validation;

import exceptions.IllegalDimensionException;
import model.path.Path;

public class DimensionValidation {
	
	public void validate(int dimension) throws IllegalDimensionException {
		if (dimension < Path.MIN_DIMENSION || dimension > Path.MAX_DIMENSION)
			throw new IllegalDimensionException("Only square matrices with minimal dimension of 7X7 and maximal of 10x10 are supported.");
	}
}
