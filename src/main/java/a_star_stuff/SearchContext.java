package a_star_stuff;

import java.util.List;

/**
 * Interface provided solely for identification reasons.
 * i.e. Just tag implementing classes / inheriting interfaces
 * as {@linkplain SearchContext}s, without specifying
 * any behavior.
 */
public interface SearchContext<Domain, Position> {

    /**
     * Setter for the domain.
     */
    void setDomain(Domain domain);

    /**
     * Getter for the domain.
     */
    Domain domain();

    /**
     * Setter for positions.
     * @param start position.
     * @param goal position.
     */
    void endpoints(Position start, Position goal);

    /**
     * Getter for the starting Position.
     */
    Position start();

    /**
     * Getter for the target Position.
     */
    Position target();

    /**
     * Getter for the solution
     */
    List<Position> solution();

    /**
     * Setter for the Cost function.
     */
    void cost(Cost<SearchContext<Domain, Position>, Position> cost);

    /**
     * Setter for the Estimation function.
     */
    void estimation(Estimation<SearchContext<Domain, Position>, Position> est);

    /**
     * Setter for the Expansion function.
     */
    void expansion(Expansion<SearchContext<Domain, Position>, Position> exp);

    /**
     * Method to initiate execution of the com.group1.octobots.search.
     */
    SearchContext<Domain, Position> execute();

    /**
     * Method for setting all the fields of the SearchContext back to their default values.
     * This enables the re-using of the same object for performing another com.group1.octobots.search with
     * different strategies.
     */
    void clear();
}
