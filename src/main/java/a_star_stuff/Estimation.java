package a_star_stuff;

/**
 * Created on 3/27/2017.
 */
@FunctionalInterface
public interface Estimation<Context, Position> {
    float estimate(Context c, Position from, Position to);
}
