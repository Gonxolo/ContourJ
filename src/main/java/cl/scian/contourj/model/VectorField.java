package cl.scian.contourj.model;

public final class VectorField {

    private final double[][] u;
    private final double[][] v;

    public VectorField(double[][] u, double[][] v){
        this.u = u;
        this.v = v;
    }

    public double[][] getU() {
        return u;
    }

    public double[][] getV() {
        return v;
    }

}
