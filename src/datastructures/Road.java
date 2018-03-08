package datastructures;

public class Road {

	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int length;
	private boolean two_way;
	
	public Road(Intersection intersection_from, Intersection intersection_to) {
		this.x1 = intersection_from.getXCoord();
		this.y1 = intersection_from.getYCoord();
		this.x2 = intersection_to.getXCoord();
		this.y2 = intersection_to.getYCoord();
		
		this.length = this.calcLength(x1, y1, x2, y2);
	}
	
	public Road(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.length = this.calcLength(x1, y1, x2, y2);	
	}
	
	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	public boolean isTwoWay() {
		return two_way;
	}

	public void setTwoWay(boolean two_way) {
		this.two_way = two_way;
	}
	
	public String toString() {
		return "Road: (" + this.x1 + ", " + this.y1 + ") -> (" + this.x2 + ", " + this.y2 + ")";
	}
	
	public boolean equalCoordinatesWith(Road road) {
		if (this.x1 == road.getX1() && this.y1 == road.getY1() && this.x2 == road.getX2() && this.y2 == road.getY2()) {
			return true;
		}
		
		return false;
	}
	
	private int calcLength(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow(y2 - y1, 2));
	}

}
