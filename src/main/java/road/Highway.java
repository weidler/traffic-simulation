package road;

import datastructures.Intersection;
import datastructures.StreetMap;
import datatype.Point;
import type.RoadType;

public class Highway extends Road {

	public Highway(Intersection intersection_from, Intersection intersection_to) {
		super(intersection_from, intersection_to);
	}

	public Highway(int x1, int y1, int x2, int y2) {
		super(x1, y1, x2, y2);
	}

	public Highway(Point A, Point B, StreetMap streetMap, int lanes) {
		super(A, B, streetMap, lanes);
	}

	public Highway(Intersection a, Intersection b, StreetMap streetMap, int lanes) {
		super(a, b, streetMap, lanes);
	}

	protected void setTypeParameters() {
		type = RoadType.HIGHWAY;
		allowed_max_speed = 120;
	}
}
