package algorithms;

import java.util.Arrays;
import java.util.List;

public interface Graph<Node, Edge> {
	List<Node> getNeighbours(Node n);
	List<Edge> getAdjacentEdges(Node n);
	double getWeight(Edge e);
	
	// example for astar
	enum Action{
		SUBSTRACT(1,-3), ADD(2,5);

		private double cost;
		private double valToAdd;

		Action(double cost, double valToAdd){
			this.cost = cost;
			this.valToAdd = valToAdd;
		}
		
		double apply(double other) {
			return other + this.valToAdd;
		}
		
	}
	
	public static void main(String[] args) {
		Astar<Double, Action> astar = new Astar<>();
		Double goal = 101.0;
		Astar<Double, Action>.PQNode searchResult = astar.search((Double) 0.0, //start 
				number -> number.equals(goal), //isTarget 
				number -> Arrays.asList(Action.values()), //expansion function
				number -> Math.abs(goal - number), // estimation function
				(number, action, result) -> action.cost, //cost function
				(number, action) -> action.apply(number)); //transfer function
	
	List<Action> actionsTaken = astar.getActionSequence(searchResult);
	List<Double> numbersToGoal = astar.makePath(searchResult);
	System.out.println(actionsTaken);
	System.out.println(numbersToGoal);
	}
	
}
