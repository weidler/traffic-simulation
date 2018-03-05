package controller.numericalmethods;

/**
 * Created by Savvas on 3/5/2018.
 */
public class SimpsonIntegrator {

    Function function;
    double[] xS;
    double[] yS;

    public SimpsonIntegrator(){}

    public SimpsonIntegrator(Function function){
        this.function = function;
    }

    /**
     * Uses composite Simpson method to integrate
     * @param a low part of integral
     * @param b upper part of integral
     * @param n number of steps, must be even!
     * @return the value of the integral from a to b
     * with around 30 steps, for exponential function gives result very close to that of MATLAB, with abs. error 0.01
     */
    public double integrate(double a, double b, int n){

        if(n%2!=0) throw new IllegalArgumentException("Number of steps must be even!");

        double h = (b-a)/(double) n; //calculate time step

        xS = new double[n+1];

        //make array of x values
        for(int i=0; i<xS.length; i++){
            xS[i] = a + i*h;
        }

        yS = new double[n+1];

        //make array of y values
        for(int i=0; i<yS.length; i++){
            yS[i] = function.getY(xS[i]);
        }

        double integral = h*(yS[0] + yS[n-1] + 2*sumEven(n) +4*sumOdd(n))/3;

        return integral;

    }

    //sum the even xS
    private double sumEven(int n){

        double sum = 0;

        for(int i=1; i<(n/2);i++){
            sum += yS[2*i];
        }

        return sum;

    }

    //sum the odd xS
    private double sumOdd(int n){

        double sum = 0;

        for(int i=1;i<=n/2;i++){
            sum += yS[(2*i)-1];
        }

        return sum;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
