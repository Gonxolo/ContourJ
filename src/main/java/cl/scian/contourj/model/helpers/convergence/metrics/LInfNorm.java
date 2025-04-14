package cl.scian.contourj.model.helpers.convergence.metrics;

import cl.scian.contourj.model.Contour;

import java.util.Arrays;

import static cl.scian.contourj.model.helpers.ContourUtils.deltaMag;

public class LInfNorm implements ConvergenceMetric {

    private final String NAME = "L-Infinity Norm";

    @Override
    public double calculate(Contour originalContour, Contour adjustedContour) {
        return Arrays.stream(deltaMag(originalContour, adjustedContour)).map(Math::abs).max().orElse(0.0d);
    }

    public String getName() {
        return this.NAME;
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
