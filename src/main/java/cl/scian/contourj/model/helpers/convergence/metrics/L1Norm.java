package cl.scian.contourj.model.helpers.convergence.metrics;

import cl.scian.contourj.model.Contour;

import java.util.Arrays;

import static cl.scian.contourj.model.helpers.ContourUtils.deltaMag;

public class L1Norm implements ConvergenceMetric {

    private final String NAME = "L1 Norm";
    @Override
    public double calculate(Contour originalContour, Contour adjustedContour) {
        return Arrays.stream(deltaMag(originalContour, adjustedContour)).map(Math::abs).sum();
    }

    public String getName() {
        return this.NAME;
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
