package physics;

import java.text.DecimalFormat;

import javax.vecmath.Vector2d;
/**
 * Physics class (to be used mainly by the car Controller probably)
 * @author Savvas
 */
public class Engine {

    public static final DecimalFormat df = new DecimalFormat("#.##");
    public static final double PI = Double.parseDouble(df.format(Math.PI));
    public static final double TOL = 0.01; //any difference that exceeds this tolerance will be rounded


    /**
     *
     * @param vector The vector to be rotated
     * @param angle The angle we want to rotate the vector by, in radians
     * @return The rotated vector which has the same length as the original vector
     * Check PhysicsTest class for an animation of how the method works
     */
    public Vector2d rotate(Vector2d vector, double angle){

        double length = vector.length();

        double x = vector.x*Math.cos(angle) - vector.y*Math.sin(angle);
      //  System.out.println("x: " + x);
        double y = vector.x*Math.sin(angle) + vector.y*Math.cos(angle);
       // System.out.println("y: " + y);

        double xRounded = Math.round(x);
        double yRounded = Math.round(y);

        if(Math.abs(xRounded - x) < TOL)
            x = Math.round(x);

        if(Math.abs(yRounded - y) < TOL)
            y = Math.round(y);

        //System.out.println("x: " + x);
        //System.out.println("y: " + y);

        return new Vector2d(x,y);
    }


    /**
     *
     * @param initial_pos the initial position of the object
     * @param vel the velocity
     * @param acc the acceleration
     * @param time the time interval (delta of time, simply time if t0 = 0)
     * @return the new position of the object
     */
    public Vector2d move(Vector2d initial_pos, Vector2d vel, Vector2d acc, double time){

        double x = initial_pos.x;
        double y = initial_pos.y;

        x += vel.x*time + 0.5*acc.x*Math.pow(time,2);
        y += vel.y*time + 0.5*acc.y*Math.pow(time,2);

        return new Vector2d(x,y);

    }

}