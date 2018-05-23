package road;

import datastructures.Intersection;
import type.RoadType;

public class DirtRoad extends Road {

	public DirtRoad(Intersection intersection_from, Intersection intersection_to) {
		super(intersection_from, intersection_to);
	}

	public DirtRoad(int x1, int y1, int x2, int y2) {
		super(x1, y1, x2, y2);
	}
	
	protected void setTypeParameters() {
		type = RoadType.DIRT_ROAD;
		allowed_max_speed = 30;	
	}
}
