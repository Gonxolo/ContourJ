package cl.scian.contourj.model.helpers.convergence.metrics;

import cl.scian.contourj.model.exceptions.UnequalArrayLengthException;
import cl.scian.contourj.model.Contour;

import java.util.Arrays;

public final class old_ConvergenceMetrics {

    private static final double INITIAL_MIN_DISTANCE = Double.MAX_VALUE;
    private static final double INITIAL_MAX_DISTANCE = 0.0d;

    private old_ConvergenceMetrics() {}

    public static double average(double[] array) {
        return Arrays.stream(array).average().orElse(0.0d);
    }

    public static double hausdorffDistance(double[] polygon1X, double[] polygon1Y, double[] polygon2X, double[] polygon2Y) throws UnequalArrayLengthException {
        return hausdorffDistance(new Contour(polygon1X, polygon1Y, 0, 0), new Contour(polygon2X, polygon2Y, 0, 0));
    }

    public static double hausdorffDistance(Contour polygon1, Contour polygon2) {
        double maxMinDistanceFrom1To2 = calculateMaxMinDistance(polygon1, polygon2);
        double maxMinDistanceFrom2To1 = calculateMaxMinDistance(polygon2, polygon1);
        return Math.max(maxMinDistanceFrom1To2, maxMinDistanceFrom2To1);
    }

    private static double calculateMaxMinDistance(Contour polygon1, Contour polygon2) {
        int vertexCount1 = polygon1.numberOfPoints();
        int vertexCount2 = polygon2.numberOfPoints();
        double maxMinDistance = INITIAL_MAX_DISTANCE;
        for (int i = 0; i < vertexCount1; i++) {
            double minDistance = INITIAL_MIN_DISTANCE;
            for (int j = 0; j < vertexCount2; j++){
                double distance = Math.hypot(polygon2.getX()[j] - polygon1.getX()[i], polygon2.getY()[j] - polygon1.getY()[i]);
                minDistance = Math.min(minDistance, distance);
            }
            maxMinDistance = Math.max(maxMinDistance, minDistance);
        }
        return maxMinDistance;
    }

    public static double l1Norm(double[] array) {
        return Arrays.stream(array).map(Math::abs).sum();
    }

    public static double l2Norm(double[] array) {
        return Math.sqrt(Arrays.stream(array).map(v -> v * v).sum());
    }

    public static double lInfinityNorm(double[] array) {
        return Arrays.stream(array).map(Math::abs).max().orElse(0.0);
    }

}
