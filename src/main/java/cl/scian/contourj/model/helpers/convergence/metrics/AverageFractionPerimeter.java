package cl.scian.contourj.model.helpers.convergence.metrics;

import cl.scian.contourj.model.Contour;

import java.util.Arrays;

import static cl.scian.contourj.model.helpers.ContourUtils.deltaMag;

public class AverageFractionPerimeter implements ConvergenceMetric {

    private final String NAME = "Average Distance : Perimeter";

    @Override
    public double calculate(Contour originalContour, Contour adjustedContour) {
        return Arrays.stream(deltaMag(originalContour, adjustedContour)).average().orElse(0.0d) / adjustedContour.getPerimeter();
    }

    public String getName() {
        return this.NAME;
    }

    @Override
    public String toString() {
        return this.getName();
    }

}