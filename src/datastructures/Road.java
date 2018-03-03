package datastructures;

public class Road {

	int x1;
	int y1;
	int x2;
	int y2;
	private int length;  // probably replace by checking distance between Intersections
	private boolean two_way;
	
	public Road(int x1, int y1, int x2, int y2)
	{
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;
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

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isTwo_way() {
		return two_way;
	}

	public void setTwo_way(boolean two_way) {
		this.two_way = two_way;
	}

}
