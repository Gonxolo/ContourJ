package cl.scian.contourj.model.helpers.convergence.metrics;

import cl.scian.contourj.model.Contour;

import java.util.Arrays;

import static cl.scian.contourj.model.helpers.ContourUtils.deltaMag;

public class L2Norm implements ConvergenceMetric{

    private final String NAME = "L2 Norm";

    @Override
    public double calculate(Contour originalContour, Contour adjustedContour) {
        return Math.sqrt(Arrays.stream(deltaMag(originalContour, adjustedContour)).map(v -> v * v).sum());
    }

    public String getName() {
        return this.NAME;
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
