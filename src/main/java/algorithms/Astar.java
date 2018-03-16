package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

@SuppressWarnings("unchecked")

public class Astar<State, Action> {
    public PQNode mostPromising;
    private PriorityQueue<PQNode> pq;
    private HashMap<State,PQNode> toPQNode; //contains all visited states and states in the pq

    public Astar(){
        pq = new PriorityQueue<>(Comparator.comparingDouble((PQNode v) -> v.priority));
        toPQNode = new HashMap<>();
    }
    
    /**
     * Launches an A* search from a start Node/State. Expands new nodes until a target is found (e.g. isTarget.apply(node) returns true).
     * Note that the States returned by the transferFunc need to implement their own equals() and hashcode() methods and
     * that all nodes discovered during the search are kept in memory in order to avoid exploring a State multiple times if it is part of a cycle.
     * @param start initial state
     * @param isTarget a Predicate, return true if the given Node is the target
     * @param expansionFunc given a State, return all available Actions
     * 
     * @param estimationFunc given a State return the estimated cost to a target according to some heuristic. 
     * 						 Note that this heuristic needs to always return a value less or equal to the actual
     *  					 cost remaining to the goal in order to guarantee that the path returned is indeed the
     *  			         shortest path (e.g. admissable).
     * 
     * @param costFunc	is called with costFunc.applyToDouble(current,a,next) and returns the cost taking Action 'a' on State 'current' to reach the State 'next'. 
     * @param transferFunc given a State s, and an Action a, return the State reached when a is applied to s.
     * @return a result, use it for makePath() and getActionSequence()
     */
    public PQNode search(State start, 
    		Predicate<State> isTarget,
    		Function<State,List<Action>> expansionFunc,
    		ToDoubleFunction<State> estimationFunc,
    		ToDoubleTriFunction<State,Action,State> costFunc,
    		BiFunction<State,Action,State> transferFunc){
        
        PQNode pivot = new PQNode(start, null, null, 0,0);
    	
        if(isTarget.test(start))
            return pivot;

        pivot.visited = true; //no need to visit the root
        mostPromising = pivot;
        toPQNode.put(start, pivot);
        pq.add(pivot);
        boolean foundSolution = false;
        
        while(!pq.isEmpty()){
            
        	//poll top element of pq, visit
            pivot = pq.poll();
            pivot.visited = true;
            
            State state = pivot.element;
            
            foundSolution = isTarget.test(state);
            if(foundSolution) break;
        
            //expand from pivot, compute priority of new nodes, insert into pq
            List<Action> availableActions = expansionFunc.apply(state);
            for(Action a : availableActions){
            	
            	State neighbour = transferFunc.apply(state, a);
                double distance = pivot.distance + costFunc.applyAsDouble(state, a, neighbour);
                double estimate = estimationFunc.applyAsDouble(neighbour);
  
                PQNode queuedNode = toPQNode.get(neighbour);

                if(queuedNode != null){
                    //is it possible that this resolves to true, even though the node was visited and the estimationFunc always underestimates?
                    if(distance < queuedNode.distance) {
                        //update it's position in the pq, needs to be removed+readded (= n+log(n) time) for that :( there may be a workaround
                        queuedNode.update(pivot, a, distance + estimate, distance);
                        //do i need that check?
                        if (!queuedNode.visited) pq.remove(queuedNode);
                        pq.add(queuedNode);
                    }
                    
                } else {
                    PQNode candidate = new PQNode(neighbour, a, pivot, estimate + distance, distance);
                    toPQNode.put(neighbour, candidate);
                    pq.add(candidate);
                    if(mostPromising.priority - mostPromising.distance > estimate)
                        mostPromising = candidate;
                }
            }
        }
        
        pq.clear();
        toPQNode.clear();
        
        return pivot;
    }
    /**
     * Builds a path, a sequence of states, from the given initial state to a target state 
     * based on the result of search()
     * @param end
     * @return list of states
     */
    public List<State> makePath(PQNode end){
        ArrayList<State> path = new ArrayList<>();

        while(end != null) {
            path.add(end.element);
            end = end.prior;
        }
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Builds a sequence of actions based on the result of search leading to the goal state if applied consecutively on the initial state.
     * @param end
     * @return list of actions
     */
    public List<Action> getActionSequence(PQNode end){
        ArrayList<Action> seq = new ArrayList<>();

        while(end != null) {
            seq.add(end.via);
            end = end.prior;
        }
        seq.remove(seq.size()-1);
        Collections.reverse(seq);
        return seq;
    }

    public final class PQNode{
        State element;
        PQNode prior;
        Action via;
        double priority;
        double distance;
        boolean visited = false;
        // via is the action the State held (aka PQNode.element) is reached.
        PQNode(State element, Action via, PQNode prior, double priority, double distance){
            this.element = element;
            //sets the other values
            update(prior, via, priority,distance);
        }
        final void update(PQNode prior, Action via, double priority, double distance){
            this.prior = prior;
            this.priority = priority;
            this.distance = distance;
            this.via = via;
        }
    }
}