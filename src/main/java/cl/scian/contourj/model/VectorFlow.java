package cl.scian.contourj.model;

import static java.lang.Math.*;

public abstract class VectorFlow {

    protected final double[][] image;
    protected final double mu;
    protected final int gvf_iterations;
    protected final VectorField vectorField;

    public VectorFlow(int[][] image, double mu, int gvf_iterations) {
        this.image = new double[image.length][image[0].length];
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                this.image[i][j] = image[i][j];
            }
        }
        this.mu = mu;
        this.gvf_iterations = gvf_iterations;
        this.vectorField = this.calculate();
    }

    public VectorFlow(float[][] image, double mu, int gvf_iterations) {
        this.image = new double[image.length][image[0].length];
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                this.image[i][j] = image[i][j];
            }
        }
        this.mu = mu;
        this.gvf_iterations = gvf_iterations;
        this.vectorField = this.calculate();
    }

    protected double[][] gradient(double[][] image, int direction) {
        double[][] gradient = new double[image.length][image[0].length];

        if (direction == 0) {
            for (int i = 0; i < gradient.length; i++) {
                for (int j = 0; j < gradient[i].length; j++) {
                    gradient[i][j] = (image[i][Math.floorMod(j+1, image[i].length)] - image[i][Math.floorMod(j-1, image[i].length)]) * 0.5;
                }
            }
            for (int i = 0; i < gradient.length; i++) {
                double secondElementOfRow = gradient[i][Math.floorMod(1, gradient[i].length)];
                double secondToLastElementOfRow = gradient[i][Math.floorMod(gradient[i].length-2, gradient[i].length)];
                gradient[i][gradient[i].length-1] = secondToLastElementOfRow;
                gradient[i][0] = secondElementOfRow;
            }
        }

        if (direction == 1) {
            for (int i = 0; i < gradient.length; i++) {
                for (int j = 0; j < gradient[i].length; j++) {
                    gradient[i][j] = (image[Math.floorMod(i + 1, image.length)][j] - image[Math.floorMod(i - 1, image.length)][j]) * 0.5;
                }
            }
            double[] secondRow = gradient[Math.floorMod(1, gradient.length)].clone();
            double[] secondToLastRow = gradient[Math.floorMod(gradient.length-2, gradient.length)].clone();
            gradient[0] = secondRow;
            gradient[gradient.length-1] = secondToLastRow;
        }

        return gradient;
    }

    protected VectorField edgeMap(double[][] image) {
        double edgeMapMaxValue = Double.MIN_VALUE;
        double edgeMapMinValue = Double.MAX_VALUE;

        double[][] xImageGradient = gradient(image, 0);
        double[][] yImageGradient = gradient(image, 1);

        double[][] edgeMap = new double[image.length][image[0].length];
        for (int i = 0; i < edgeMap.length; i++) {
            for (int j = 0; j < edgeMap[0].length; j++) {
                double res = Math.hypot(xImageGradient[i][j], yImageGradient[i][j]);
                edgeMapMaxValue = max(res, edgeMapMaxValue);
                edgeMapMinValue = min(res, edgeMapMinValue);
                edgeMap[i][j] = res;
            }
        }
        // hypot(gradient((double) this.image, 0), gradient((double) this.image, 1));

        if (edgeMapMaxValue != edgeMapMinValue) {
            for (int i = 0; i < edgeMap.length; i++) {
                for (int j = 0; j < edgeMap[i].length; j++) {
                    edgeMap[i][j] = (edgeMap[i][j] - edgeMapMinValue) / (edgeMapMaxValue - edgeMapMinValue);
                }
            }
        }

        return new VectorField(gradient(edgeMap, 0), gradient(edgeMap, 1));
    }

    // Rows are the pixels from the image from left to right
    // Cols are the pixels from the image form the top to the bottom
    protected double[][] convolution(double[][] image, double[][] kernel) {
        double[][] resultingImage = new double[image.length][image[0].length];
        for (int imageRow = 0; imageRow < image.length; imageRow++) {
            for (int imageColumn = 0; imageColumn < image[imageRow].length; imageColumn++) {
                resultingImage[imageRow][imageColumn] = 0.0d;
                for (int i = 0; i < kernel.length; i++) {
                    int imageKernelTruncatedRow = min(max(0, imageRow - kernel.length/2 + i), image.length - 1);
                    for (int j = 0; j < kernel[i].length; j++) {
                        int imageKernelTruncatedColumn = min(max(0, imageColumn - kernel[i].length/2 + j), image[imageRow].length - 1);
                        resultingImage[imageRow][imageColumn] += image[imageKernelTruncatedRow][imageKernelTruncatedColumn] * kernel[i][j];
                    }
                }
            }
        }
        return resultingImage;
    }

    protected double[][] laplacian(double[][] image) {
        double[][] LAPLACIAN_KERNEL = {
                {0.0000000, 0.0000000, 0.0833333, 0.0000000, 0.0000000},
                {0.0000000, 0.0833333, 0.0833333, 0.0833333, 0.0000000},
                {0.0833333, 0.0833333, -1.000000, 0.0833333, 0.0833333},
                {0.0000000, 0.0833333, 0.0833333, 0.0833333, 0.0000000},
                {0.0000000, 0.0000000, 0.0833333, 0.0000000, 0.0000000}
        };
        return convolution(image, LAPLACIAN_KERNEL);
    }

    private VectorField calculate() {

        VectorField edgeMap = this.edgeMap(this.image);

        double[][] u = edgeMap.getU();
        double[][] v = edgeMap.getV();

        // original version for the GGVF by Xu99
        // b = (*self.pU)^2+(*self.pV)^2
        double[][] b = new double[u.length][u[0].length];
        for (int i = 0; i < b.length; i++){
            for (int j = 0; j < b[i].length; j++){
                b[i][j] = abs(u[i][j]) + abs(v[i][j]);
            }
        }
        // b = abs(this.u) + abs(this.v)

        double[][] g = new double[b.length][b[0].length];
        for (int i = 0; i < g.length; i++){
            for (int j = 0; j < g[i].length; j++){
                g[i][j] = exp(-b[i][j] / this.mu);
            }
        }
        // g = exp(-b / this.mu);

        double[][] c1 = new double[u.length][u[0].length];
        for (int i = 0; i < c1.length; i++){
            for (int j = 0; j < c1[i].length; j++){
                c1[i][j] = u[i][j] * (1 - g[i][j]);
            }
        }
        // c1 = this.u * (1-g);

        double[][] c2 = new double[v.length][v[0].length];
        for (int i = 0; i < c2.length; i++){
            for (int j = 0; j < c2[i].length; j++){
                c2[i][j] = v[i][j] * (1 - g[i][j]);
            }
        }
        // c2 = this.v * (1-g);

        for (int iterationNumber = 1; iterationNumber <= this.gvf_iterations; iterationNumber++) {
            double[][] uLaplacian = laplacian(u);
            double[][] vLaplacian = laplacian(v);

            for (int i = 0; i < u.length; i++) {
                for (int j = 0; j < u[i].length; j++) {
                    u[i][j] = g[i][j] * (u[i][j] + uLaplacian[i][j]) + c1[i][j];
                }
            }

            for (int i = 0; i < v.length; i++) {
                for (int j = 0; j < v[i].length; j++) {
                    v[i][j] = g[i][j] * (v[i][j] + vLaplacian[i][j]) + c2[i][j];
                }
            }

        }

        return new VectorField(u, v);

    }

    public double[][] getU() {
        return this.vectorField.getU();
    }

    public double[][] getV() {
        return this.vectorField.getV();
    }

}
