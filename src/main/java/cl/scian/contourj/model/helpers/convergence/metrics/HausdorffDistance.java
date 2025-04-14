package cl.scian.contourj.model.helpers.convergence.metrics;

import cl.scian.contourj.model.Contour;

public class HausdorffDistance implements ConvergenceMetric {

    private final String NAME = "Hausdorff Distance";

    @Override
    public double calculate(Contour originalContour, Contour adjustedContour) {
        double maxMinDistanceFrom1To2 = calculateMaxMinDistance(adjustedContour, originalContour);
        double maxMinDistanceFrom2To1 = calculateMaxMinDistance(originalContour, adjustedContour);
        return Math.max(maxMinDistanceFrom1To2, maxMinDistanceFrom2To1);
    }

    private static double calculateMaxMinDistance(Contour polygon1, Contour polygon2) {
        int vertexCount1 = polygon1.numberOfPoints();
        int vertexCount2 = polygon2.numberOfPoints();
        double maxMinDistance = 0.0d;
        for (int i = 0; i < vertexCount1; i++) {
            double minDistance = Double.MAX_VALUE;
            for (int j = 0; j < vertexCount2; j++){
                double distance = Math.hypot(polygon2.getX()[j] - polygon1.getX()[i], polygon2.getY()[j] - polygon1.getY()[i]);
                minDistance = Math.min(minDistance, distance);
            }
            maxMinDistance = Math.max(maxMinDistance, minDistance);
        }
        return maxMinDistance;
    }

    public String getName() {
        return this.NAME;
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
