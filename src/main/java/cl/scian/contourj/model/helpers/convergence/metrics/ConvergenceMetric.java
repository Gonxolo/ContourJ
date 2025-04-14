package cl.scian.contourj.model.helpers.convergence.metrics;

import cl.scian.contourj.model.Contour;

public interface ConvergenceMetric {

    public double calculate(Contour originalContour, Contour adjustedContour);

    public String getName();

}
