package cl.scian.contourj.model.helpers.interpolation;

public class SplineInterpolation {

    public static double[] splineInit(double[] x, double[] y){
        return splineInit(x, y, Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public static double[] splineInit(double[] x, double[] y, double yp0, double ypn_1){
        int n = x.length;
        double[] y2 = new double[n];
        double[] u = new double[n];

        if (yp0 > 0.99e30){
            y2[0] = 0.0d;
            u[0] = 0.0d;
        } else{
            y2[0] = -0.5d;
            u[0] = (3.0d / (x[1] - x[0])) * ((y[1] - y[0]) / (x[1] - x[0]) - yp0);
        }

        for (int i=1; i<n-1; i++){
            double sigma = (x[i] - x[i-1]) / (x[i+1] - x[i-1]);
            double p = sigma * y2[i-1] + 2.0d;
            y2[i] = (sigma - 1.0d) / p;
            u[i] = (y[i+1] - y[i]) / (x[i+1] - x[i]) - (y[i] - y[i-1]) / (x[i] - x[i-1]);
            u[i] = (6.0d * u[i] / (x[i+1] - x[i-1]) - sigma * u[i-1]) / p;
        }

        double qn_1 = ypn_1 > 0.99e30 ? 0.0d : 0.5d;
        double un_1 = ypn_1 > 0.99e30 ? 0.0d : (3.0 / (x[n-1] - x[n-2])) * (ypn_1 - (y[n-1] - y[n-2]) / (x[n-1] - x[n-2]));

        y2[n-1] = (un_1 - qn_1 * u[n-2]) / (qn_1 * y2[n-2] + 1.0d);

        for (int k=n-2; k>=0; k--){
            y2[k] = y2[k] * y2[k+1] + u[k];
        }

        return y2;
    }

    public static double[] splineInterp(double[] x, double[] y, double[] y2, double[] x2){ //throws Exception {
        double[] resultingInterpolation = new double[x2.length];
        for (int i=0; i < x2.length; i++) {
            int klo = 0;
            int khi = x.length - 1;
            while (khi - klo > 1) {
                int k = (khi + klo) >> 1;
                if (x[k] > x2[i]) khi = k;
                else klo = k;
            }
            double h = x[khi] - x[klo];
            // if (h == 0.0) throw new Exception("Bad xa input to routine splint");
            double a = (x[khi] - x2[i]) / h;
            double b = (x2[i] - x[klo]) / h;
            resultingInterpolation[i] = a * y[klo] + b * y[khi] + ((a * a * a - a) * y2[klo] +
                    (b * b * b - b) * y2[khi]) * (h * h) / 6.0;
        }
        return resultingInterpolation;
    }

}
