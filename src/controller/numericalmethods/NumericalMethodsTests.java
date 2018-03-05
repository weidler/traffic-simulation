package controller.numericalmethods;

/**
 * Created by Savvas on 3/5/2018.
 */
public class NumericalMethodsTests {

    public static void main(String[] args){

        SimpsonIntegrator integrator = new SimpsonIntegrator(new Exponential());

        System.out.println(integrator.integrate(0,2,30));

        Differentiator differentiator = new Differentiator(new Exponential(),10,0,2);

        System.out.println(differentiator.differentiate(2));
    }
}
