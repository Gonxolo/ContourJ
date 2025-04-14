package cl.scian.contourj.model.helpers;

import cl.scian.contourj.model.Contour;

import java.util.stream.IntStream;

public final class ContourUtils {
    private ContourUtils() {} // Prevent instantiation

    public static double[] deltaMag(Contour originalContour, Contour adjustedContour) {
        double[] xDelta = IntStream.range(0, originalContour.numberOfPoints())
                .mapToDouble(i -> Math.abs(originalContour.getX()[i] - adjustedContour.getX()[i]))
                .toArray();

        double[] yDelta = IntStream.range(0, originalContour.numberOfPoints())
                .mapToDouble(i -> Math.abs(originalContour.getY()[i] - adjustedContour.getY()[i]))
                .toArray();

        return IntStream.range(0, xDelta.length)
                .mapToDouble(i -> Math.hypot(xDelta[i], yDelta[i]))
                .toArray();
    }
}
