package cl.scian.contourj.model.helpers;

import cl.scian.contourj.model.Contour;

import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static cl.scian.contourj.model.helpers.interpolation.SplineInterpolation.splineInit;
import static cl.scian.contourj.model.helpers.interpolation.SplineInterpolation.splineInterp;
import static java.lang.Math.max;

public class Sampling { // TODO: Check how arc sample is closing curves
    public static double[][] arcSample(Contour contour, int numPoints, boolean closeCurve) { // points, fClose
        numPoints = max(16, numPoints);

        double[] xInputCoordinates = contour.getX().clone(); // x_in
        double[] yInputCoordinates = contour.getY().clone(); // y_in

        int numPointsCurve = xInputCoordinates.length; // npts

        if (closeCurve){
            if ((xInputCoordinates[0] != xInputCoordinates[xInputCoordinates.length - 1]) || (yInputCoordinates[0] != yInputCoordinates[yInputCoordinates.length - 1])) {
                xInputCoordinates = Arrays.copyOf(xInputCoordinates, xInputCoordinates.length + 1);
                xInputCoordinates[xInputCoordinates.length - 1] = xInputCoordinates[0];
                yInputCoordinates = Arrays.copyOf(yInputCoordinates, yInputCoordinates.length + 1);
                yInputCoordinates[yInputCoordinates.length - 1] = yInputCoordinates[0];
            }
            numPointsCurve++;
        } else {
            numPoints--;
        }

        int nc = (numPointsCurve - 1) * 100; // nc
        double[] t = IntStream.range(0, numPointsCurve).mapToDouble(i -> i).toArray(); // t
        double[] t1 = DoubleStream.iterate(0, n -> n + 1) // t1
                .limit(nc + 1)
                .map(n -> n / 100.0)
                .toArray();
        double[] x1 = splineInterp(t, xInputCoordinates, splineInit(t, xInputCoordinates), t1); // x1
        double[] y1 = splineInterp(t, yInputCoordinates, splineInit(t, yInputCoordinates), t1); // y1

        double[] dx1;
        double[] dy1;

        if (closeCurve) {
            double avgSlopeX = (x1[1] - x1[0] + x1[nc] - x1[nc-1]) / (t1[1] - t1[0]) * 0.5;
            double avgSlopeY = (y1[1] - y1[0] + y1[nc] - y1[nc-1]) / (t1[1] - t1[0]) * 0.5;
            dx1 = splineInit(t, xInputCoordinates, avgSlopeX, avgSlopeX);
            dy1 = splineInit(t, yInputCoordinates, avgSlopeY, avgSlopeY);
        } else {
            double avgSlopeX0 = (x1[1] - x1[0]) / (t1[1] - t1[0]);
            double avgSlopeY0 = (y1[1] - y1[0]) / (t1[1] - t1[0]);
            double avgSlopeX1 = (x1[nc] - x1[nc-1]) / (t1[nc] - t1[nc-1]);
            double avgSlopeY1 = (y1[nc] - y1[nc-1]) / (t1[nc] - t1[nc-1]);
            dx1 = splineInit(t, xInputCoordinates, avgSlopeX0, avgSlopeX1);
            dy1 = splineInit(t, yInputCoordinates, avgSlopeY0, avgSlopeY1);
        }

        x1 = splineInterp(t, xInputCoordinates, dx1, t1);
        y1 = splineInterp(t, yInputCoordinates, dy1, t1);

        double[] ss = new double[x1.length];
        ss[0] = 0.0d;
        for(int i=1; i < x1.length; i++){
            ss[i] = ss[i-1] + Math.hypot(x1[i] - x1[i-1], y1[i] - y1[i-1]);
        }

        int finalNumPoints = numPoints;
        double[] sx = DoubleStream.iterate(0, n -> n + 1) // t1
                .limit(numPoints)
                .map(n -> n * (ss[nc]/ finalNumPoints))
                .toArray();
        double[] tx = splineInterp(ss, t1, splineInit(ss, t1), sx);

        double[] xOutputCoordinates = splineInterp(t, xInputCoordinates, dx1, tx);
        xOutputCoordinates = Arrays.copyOf(xOutputCoordinates, xOutputCoordinates.length + 1);
        double[] yOutputCoordinates = splineInterp(t, yInputCoordinates, dy1, tx);
        yOutputCoordinates = Arrays.copyOf(yOutputCoordinates, yOutputCoordinates.length + 1);
        if (closeCurve) {
            xOutputCoordinates[xOutputCoordinates.length - 1] = xOutputCoordinates[0];
            yOutputCoordinates[yOutputCoordinates.length - 1] = yOutputCoordinates[0];
        } else {
            xOutputCoordinates[xOutputCoordinates.length - 1] = xInputCoordinates[numPointsCurve-1];
            yOutputCoordinates[yOutputCoordinates.length - 1] = yInputCoordinates[numPointsCurve-1];
        }
        return new double[][]{xOutputCoordinates, yOutputCoordinates};
    }
}
