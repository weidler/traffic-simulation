package road;

import datastructures.Intersection;
import datastructures.RoadType;

public class Highway extends Road {

	public Highway(Intersection intersection_from, Intersection intersection_to) {
		super(intersection_from, intersection_to);
		this.setTypeParameters();
	}
	
	public Highway(int x1, int y1, int x2, int y2) {
		super(x1, y1, x2, y2);
		this.setTypeParameters();
	}
	
	private void setTypeParameters() {
		type = RoadType.HIGHWAY;
		allowed_max_speed = 120;		
	}
}
