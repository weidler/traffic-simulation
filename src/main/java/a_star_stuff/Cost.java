package a_star_stuff;

/**
 * Created on 3/27/2017.
 */
@FunctionalInterface
public interface Cost<Context, Position> {
    float compute(Context c, Position from, Position to);
}
