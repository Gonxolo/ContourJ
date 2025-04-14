package cl.scian.contourj.model;

public class GeneralizedGradientVectorFlow extends VectorFlow {

    public GeneralizedGradientVectorFlow(int[][] image, double mu, int gvf_iterations) {
        super(image, mu, gvf_iterations);
    }

    public GeneralizedGradientVectorFlow(float[][] image, double mu, int gvf_iterations) {
        super(image, mu, gvf_iterations);
    }

}
