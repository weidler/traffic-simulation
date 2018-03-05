package controller.numericalmethods;

/**
 * Differentiation class to be used by the controller
 * Tested and gives good approximation for exponential function, not tested on other functions yet
 * Created by Savvas on 3/5/2018.
 */
public class Differentiator {

    Function function;
    double[] xS;
    double[] yS;
    int n;
    double a,b,h;

    public Differentiator(){}

    /**
     *
     * @param function
     * @param n the number of steps, must be even!
     * @param a left end point of the interval
     * @param b right end point of the interval
     * Uses three point forward and backward as well as centred differentiation methods.
     * Constructs an array of x values from the interval with step h=(b-a)/n between them
     */
    public Differentiator(Function function, int n, int a, int b){

        if(n%2 != 0) throw new IllegalArgumentException("number of steps must be even!");

        this.function = function;
        this.n = n;
        this.a = a;
        this.b = b;
        this.h = (b-a)/(double) n;

        xS = new double[n+1];

        for(int i=0; i<xS.length; i++)
            xS[i] = a + (i*h);

        yS = new double[n+1];

        for(int i=0; i<xS.length; i++)
            yS[i] = function.getY(xS[i]);


    }

    /**
     *
     * @param i the index of x value from xS where we want to differentiate
     * @return an approximation of the value of the differentiation
     * !Note: index and not value must be inserted. To find index you need, keep in mind x[i] = a + i*h, so for
     * example in interval [0,2] and h = 0.2, to get the value for f'(0.4) you would need i=2
     */
    public double differentiate(int i){

        if(i<2)
            return forwardDifferentiation(i);
        else if(n-i<2)
            return backwardDifferentiation(i);
        else
            return centredDifferentiation(i);
    }

    //Centred differentiation method, generally most accurate
    private double centredDifferentiation(int i){

        if(i == 0 || i ==n) throw new IllegalArgumentException("Cannot use this method near endpoints!");

        return (yS[i+1] - yS[i-1])/(2*h);
    }

    //Three point forward differentiation method
    private double forwardDifferentiation(int i){

        if(n-i < 2) throw new IllegalArgumentException("Cannot use this method near right endpoint!");

        return (-yS[i+2] + 4*yS[i+1] - 3*yS[i])/(2*h);
    }

    //Three point backward differentiation method
    private double backwardDifferentiation(int i){

        if(i<2) throw new IllegalArgumentException("Cannot use this method near left endpoint!");

        return (yS[i-2] - 4*yS[i-1] + 3*yS[i])/(2*h);
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
