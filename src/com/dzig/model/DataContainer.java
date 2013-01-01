package com.dzig.model;

import java.util.ArrayList;
import java.util.List;

public class DataContainer {
	private ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
	
	public void update(List<Coordinate> coordinates){
		coordinates.clear();
		coordinates.addAll(coordinates);
	}
	
	public List<Coordinate> getCoordinates() {
		return coordinates;
	}
}
