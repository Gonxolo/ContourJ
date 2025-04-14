package cl.scian.contourj.model.helpers;

import java.util.Arrays;

public class MatrixOps {

    public static double[][] diagonalMatrix(double[] array){
        return diagonalMatrix(array, 0);
    }

    public static double[][] diagonalMatrix(double[] array, int offset){
        int size = array.length + Math.abs(offset);
        double[][] result = new double[size][size];
        int startRow = Math.max(0, -offset);
        int startCol = Math.max(0, offset);
        for (int i=0; i < array.length; i++){
            result[startRow + i][startCol + i] = array[i];
        }
        return result;
    }

    public static double[][] matrixAdd(double[][] addend1, double[][] addend2) {
        double[][] sum = new double[addend1.length][addend1.length];
        for (int i = 0; i < addend1.length; i++) {
            for (int j = 0; j < addend1[i].length; j++) {
                sum[i][j] = addend1[i][j] + addend2[i][j];
            }
        }
        return sum;
    }

    public static double[] matrixMultiplication(double[][] matrix, double[] array) {
        
        double[] result = new double[matrix.length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                result[i] += matrix[i][j] * array[j];
            }
        }
        
        return result;
    }
    
    public static double[][] matrixMultiplication(double[][] matrixA, double[][] matrixB){
        double[][] result = new double[matrixA.length][matrixB[0].length];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                for (int i = 0; i < matrixB.length; i++) {
                    result[row][col] += matrixA[row][i] * matrixB[i][col];
                }
            }
        }

        return result;
    }

    private static double[][] augmentMatrix(double[][] matrix) {
        int n = matrix.length;
        double[][] augmentedMatrix = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, augmentedMatrix[i], 0, n);
            augmentedMatrix[i][i + n] = 1.0;
        }
        return augmentedMatrix;
    }

    private static void swapRows(double[][] matrix, int i, int j) {
        double[] temp = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = temp;
    }

    public static double[][] invertMatrix(double[][] matrix) {
        int n = matrix.length;
        double[][] augmentedMatrix = augmentMatrix(matrix);

        for (int i = 0; i < n; i++) {
            // Partial pivoting
            int maxRowIndex = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(augmentedMatrix[k][i]) > Math.abs(augmentedMatrix[maxRowIndex][i])) {
                    maxRowIndex = k;
                }
            }
            swapRows(augmentedMatrix, i, maxRowIndex);

            double pivot = augmentedMatrix[i][i];
            if (pivot == 0) {
                throw new ArithmeticException("Matrix is singular");
            }

            // Scale row to make pivot 1
            for (int j = 0; j < 2 * n; j++) {
                augmentedMatrix[i][j] /= pivot;
            }

            // Subtract rows to make other entries in the column 0
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = augmentedMatrix[k][i];
                    for (int j = 0; j < 2 * n; j++) {
                        augmentedMatrix[k][j] -= factor * augmentedMatrix[i][j];
                    }
                }
            }
        }

        // Extract inverse from augmented matrix
        double[][] inverse = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(augmentedMatrix[i], n, inverse[i], 0, n);
        }

        return inverse;
    }

    public static double[][] makeABCMatrix(double[] a, double[] b, double[] c){
        double[][] result = diagonalMatrix(Arrays.copyOfRange(a, 0, a.length - 2), 2);
        result = matrixAdd(result, diagonalMatrix(Arrays.copyOfRange(a, a.length - 2, a.length), -(a.length - 2)));
        result = matrixAdd(result, diagonalMatrix(Arrays.copyOfRange(b, 0, b.length - 1), 1));
        result = matrixAdd(result, diagonalMatrix(Arrays.copyOfRange(b, b.length - 1, b.length), -(b.length - 1)));
        result = matrixAdd(result, diagonalMatrix(c));
        result = matrixAdd(result, diagonalMatrix(Arrays.copyOfRange(b, 0, b.length - 1), -1));
        result = matrixAdd(result, diagonalMatrix(Arrays.copyOfRange(b, b.length - 1, b.length), (b.length - 1)));
        result = matrixAdd(result, diagonalMatrix(Arrays.copyOfRange(a, 0, a.length - 2), -2));
        result = matrixAdd(result, diagonalMatrix(Arrays.copyOfRange(a, a.length - 2, a.length), (a.length - 2)));
        return result;
    }

}
