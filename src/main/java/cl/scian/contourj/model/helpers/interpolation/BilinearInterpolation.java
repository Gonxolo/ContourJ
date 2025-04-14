package cl.scian.contourj.model.helpers.interpolation;

public class BilinearInterpolation { // TODO: Fix interpolation, it's giving incorrect results

    public static double[] interpolate(double[][] points, double[] xCoordinates, double[] yCoordinates){

        double[] interpolatedArray = new double[xCoordinates.length];

        for (int i=0; i<interpolatedArray.length; i++) {
            interpolatedArray[i] = pointInterpolate(points, xCoordinates[i], yCoordinates[i]); // TODO: Check if coordinates have actually been given
        }

        return interpolatedArray;
    }

    private static double pointInterpolate(double[][] points, double x, double y) {
        int x0 = (int) Math.min(Math.max(Math.floor(x), 0), points.length - 1);
        int x1 = Math.min(Math.max(x0 + 1, 0), points.length - 1);
        int y0 = (int) Math.min(Math.max(Math.floor(y), 0), points[0].length - 1);
        int y1 = Math.min(Math.max(y0 + 1, 0), points[0].length - 1);

        double q11 = points[x0][y0];
        double q12 = points[x0][y1];
        double q21 = points[x1][y0];
        double q22 = points[x1][y1];

        double r1 = (x1 - x) * q11 + (x - x0) * q21;
        double r2 = (x1 - x) * q12 + (x - x0) * q22;

        return (y1 - y) * r1 + (y - y0) * r2;
    }

}
