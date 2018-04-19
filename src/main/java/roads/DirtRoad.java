package roads;

import datastructures.Intersection;

public class DirtRoad extends Road {
	
	private int allowed_max_speed = 30;	

	public DirtRoad(Intersection intersection_from, Intersection intersection_to) {
		super(intersection_from, intersection_to);
		// TODO Auto-generated constructor stub
	}

	public DirtRoad(int x1, int y1, int x2, int y2) {
		super(x1, y1, x2, y2);
		// TODO Auto-generated constructor stub
	}

}
