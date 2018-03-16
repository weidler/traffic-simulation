package a_star_stuff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Generic A* searching algorithm.
 *
 * Genericity at its finest.
 *
 */
public class AStarSearchContext<Domain, Position>
        implements SearchContext<Domain, Position>{

    private Domain domain;

    private Position start;
    private Position target;

    private List<Position> solution;

    private Cost      <SearchContext<Domain, Position>, Position> cost;
    private Estimation<SearchContext<Domain, Position>, Position> estimation;
    private Expansion <SearchContext<Domain, Position>, Position> expansion;

    public AStarSearchContext() {
        cost = (c, from, to) -> 1;
        estimation = (c, from, to) -> 1;
        expansion = (c, p) -> Collections.emptyList();
        solution = new ArrayList<>();
    }

    @Override
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    @Override
    public Domain domain() {
        return domain;
    }

    @Override
    public void endpoints(Position start, Position target) {
        this.start = start;
        this.target = target;
    }

    @Override
    public Position start() {
        return start;
    }

    @Override
    public Position target() {
        return target;
    }

    /**
     * Getter for the solution
     */
    @Override
    public List<Position> solution() {
        return solution;
    }

    /**
     * Setter for the Cost function.
     */
    @Override
    public void cost(Cost<SearchContext<Domain, Position>, Position> cost) {
        this.cost = cost;
    }

    /**
     * Setter for the Estimation function.
     */
    @Override
    public void estimation(Estimation<SearchContext<Domain, Position>, Position> estimation) {
        this.estimation = estimation;
    }

    @Override
    public void expansion(Expansion<SearchContext<Domain, Position>, Position> expansion) {
        this.expansion = expansion;
    }

    /**
     * Method to initiate execution of the com.group1.octobots.search.
     */
    @Override
    public SearchContext<Domain, Position> execute() {
        // Map Positions to their current score in the Priority queue.
        Map<Position, Float> combinedCosts = new HashMap<>();
        // Keep track of the previous position in the best path.
        Map<Position, Position> prev = new HashMap<>();
        // Create a priority queue for the Positions, compare their corresponding score in the cost Map.
        PriorityQueue<Position> pq = new PriorityQueue<>(Comparator.comparingDouble(combinedCosts::get));

        // Traverse the Domain to assign weights

        // Add the start Position to our data structures.
        combinedCosts.put(start, 0f);
        prev.put(start, null);
        pq.add(start);
    // With all the algorithm prep done, start the algorithm.
        Position current = pq.poll();
        // While we haven't reached the target
        while (!current.equals(target)) {
            float currentCost = combinedCosts.get(current);
            // Expand the current position
            for (Position neighbour : expansion.expand(this, current)) {
                // Compute the composite cost of the move: CurrentNode -> CurrentNeighbour
                float newCost =
                        currentCost +
                        cost.compute(this, current, neighbour) +
                        estimation.estimate(this, current, neighbour);
                // If the composite cost is smaller than the previous cost
                if (newCost < combinedCosts.getOrDefault(neighbour, Float.POSITIVE_INFINITY)) {
                    // Update the cost
                    combinedCosts.put(neighbour, newCost);
                    // Set previous in path
                    prev.put(neighbour, current);
                    // Update position in priority queue or add it absent.
                    pq.remove(neighbour);
                    pq.add(neighbour);
                }
                //todo this should do it for the algorithm, now return solution.
            }
            // Go to the next Position in the queue.
            current = pq.poll();
        }

        // Build the solution.
        do {
            solution.add(current);
        } while ((current = prev.get(current)) != start);
        solution.add(current);

        Collections.reverse(solution);

        // Return self with updated solution.
        return this;
    }

    /**
     * Method for setting all the fields of the SearchContext back to their default values.
     * This enables the re-using of the same object for performing another com.group1.octobots.search with
     * different strategies.
     */
    @Override
    public void clear() {
        cost = (c, from, to) -> 1;
        estimation = (c, from, to) -> 1;
        expansion = (c, p) -> Collections.emptyList();
        solution = new ArrayList<>();

        domain = null;
        start = null;
        target = null;
    }


}
