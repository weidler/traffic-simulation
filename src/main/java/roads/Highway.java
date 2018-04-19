package roads;

import datastructures.Intersection;

public class Highway extends Road {

	private int allowed_max_speed = 120;	
	
	public Highway(Intersection intersection_from, Intersection intersection_to) {
		super(intersection_from, intersection_to);
	}
	
	public Highway(int x1, int y1, int x2, int y2) {
		super(x1, y1, x2, y2);
	}

}
