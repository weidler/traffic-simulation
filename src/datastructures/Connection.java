package datastructures;

public class Connection {
	private Integer length;  // probably replace by checking distance between Intersections
	private boolean two_way;
	
	public Connection(int length, boolean two_way) {
		this.length = length;
		this.two_way = two_way;
	}
}
