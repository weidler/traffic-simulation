package road;

import datastructures.Intersection;
import datastructures.StreetMap;
import datatype.Point;
import type.RoadType;

public class DirtRoad extends Road {

	public DirtRoad(Intersection intersection_from, Intersection intersection_to) {
		super(intersection_from, intersection_to);
	}

	public DirtRoad(int x1, int y1, int x2, int y2) {
		super(x1, y1, x2, y2);
	}

	public DirtRoad(Point A, Point B, StreetMap streetMap, int lanes) {
		super(A, B, streetMap, lanes);
	}

	public DirtRoad(Intersection a, Intersection b, StreetMap streetMap, int lanes) {
		super(a, b, streetMap, lanes);
	}

	protected void setTypeParameters() {
		type = RoadType.DIRT_ROAD;
		allowed_max_speed = 30;
	}
}
