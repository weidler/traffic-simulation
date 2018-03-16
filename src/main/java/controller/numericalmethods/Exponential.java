package controller.numericalmethods;

/**
 * Created by Savvas on 3/5/2018.
 *
 * Used to test the numerical methods
 */
public class Exponential implements Function {

    public double getY(double x){
        return Math.exp(x);
    }
}
