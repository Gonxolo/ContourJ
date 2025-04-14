package cl.scian.contourj.model.helpers.interpolation;

import cl.scian.contourj.model.exceptions.UnequalArrayLengthException;

public class BicubicInterpolation {

    public static double[] interpolate(double[][] points, double[] xCoordinates, double[] yCoordinates) throws UnequalArrayLengthException {
        if (xCoordinates.length != yCoordinates.length){
            throw new UnequalArrayLengthException("X and Y coordinates arrays must have the same length");
        }
        double[] interpolatedArray = new double[xCoordinates.length];

        for (int i = 0; i < interpolatedArray.length; i++){
            interpolatedArray[i] = pointInterpolation(points, xCoordinates[i], yCoordinates[i]);
        }

        return interpolatedArray;
    }

    private static double pointInterpolation(double[][] points, double x, double y) {
        int numRows = points.length;
        int numCols = points[0].length;

        // Calculate integer pixel coordinates surrounding the point
        int x1 = Math.max(Math.min((int) Math.floor(x) - 1, numCols - 4), 0);
        int y1 = Math.max(Math.min((int) Math.floor(y) - 1, numRows - 4), 0);

        // Calculate the fractional part of x and y
        double dx = x - x1;
        double dy = y - y1;

        // Interpolate in the x-direction for each row of 4 points
        double[] interpolatedRow = new double[4];
        for (int i = 0; i < 4; i++) {
            interpolatedRow[i] = cubicInterpolate(
                    points[y1][x1 + i], points[y1 + 1][x1 + i], points[y1 + 2][x1 + i], points[y1 + 3][x1 + i], dx);
        }

        // Interpolate in the y-direction using the interpolated row values
        return cubicInterpolate(interpolatedRow[0], interpolatedRow[1], interpolatedRow[2], interpolatedRow[3], dy);
    }

    // Cubic interpolation function
    private static double cubicInterpolate(double y0, double y1, double y2, double y3, double mu) {
        double a0 = y3 - y2 - y0 + y1;
        double a1 = y0 - y1 - a0;
        double a2 = y2 - y0;

        return a0 * mu * mu * mu + a1 * mu * mu + a2 * mu + y1;
    }
}
