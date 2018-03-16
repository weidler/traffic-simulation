package datastructures;

import java.util.*;

public class Node {

	private int id;
	private int x;
	private int y;

	private List<Node> shortestPath = new LinkedList<>();

	private Integer distance = Integer.MAX_VALUE;

	Map<Node, Integer> adjacentNodes = new HashMap<>();

	public void addDestination(Node destination, int distance) {
		adjacentNodes.put(destination, distance);
	}

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// getters and setters
}
