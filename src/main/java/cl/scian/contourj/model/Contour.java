package cl.scian.contourj.model;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.primitives.Floats;

import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.process.FloatPolygon;
import net.imglib2.RealPoint;

public final class Contour implements Comparable<Contour> {

    public static Color DEFAULT_COLOR = Color.CYAN;
    public static int xIndex = 0;
    public static int yIndex = 1;
    private static int idCounter = 0;

    private final int frame;
    private final int depth;

    private int id; // TODO: ids aren't giving much info, mainly the copied contours, because their ids differ from originals
    private List<RealPoint> points;

    private Color color = DEFAULT_COLOR;

    public Contour(List<RealPoint> points, int frame, int depth) {
        assignID();
        this.points = points;
        this.frame = frame;
        this.depth = depth;
    }

    public Contour(double[] x, double[] y, int frame, int depth) {
        assignID();
        this.points = IntStream.range(0, x.length).mapToObj(i -> new RealPoint(x[i], y[i]))
                .collect(Collectors.toList());
        this.frame = frame;
        this.depth = depth;
    }

    public Contour(float[] x, float[] y, int frame, int depth) {
        assignID();
        this.points = IntStream.range(0, x.length).mapToObj(i -> new RealPoint(x[i], y[i]))
                .collect(Collectors.toList());
        this.frame = frame;
        this.depth = depth;
    }

    public Contour(Contour contour) {
        assignID();
        this.points = contour.points;
        this.frame = contour.frame;
        this.depth = contour.depth;
    }

    public void update(double[] x, double[] y) {
        this.points = IntStream.range(0, x.length).mapToObj(i -> new RealPoint(x[i], y[i]))
                .collect(Collectors.toList());
    }

    public void update(List<RealPoint> points) {
        this.points = points;
    }

    public int getFrame() {
        return this.frame;
    }

    public int getDepth() {
        return this.depth;
    }

    public int getId() {
        return this.id;
    }

    private int getSize() {
        return this.points.size();
    }

    public int numberOfPoints() {
        return this.getSize();
    }

    public double[] getX() {
        return this.points.stream().mapToDouble(p -> p.getDoublePosition(xIndex)).toArray();
    }

    public double[] getY() {
        return this.points.stream().mapToDouble(p -> p.getDoublePosition(yIndex)).toArray();
    }

    public float[] getXAsFloat() {
        return Floats.toArray(this.points.stream().mapToDouble(p -> p.getDoublePosition(xIndex)).boxed()
                .collect(Collectors.toList()));
    }

    public float[] getYAsFloat() {
        return Floats.toArray(this.points.stream().mapToDouble(p -> p.getDoublePosition(yIndex)).boxed()
                .collect(Collectors.toList()));
    }

    public Color getColor() {
        return this.color;
    }

    public String getColorAsHex() {
        return String.format("#%02x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Roi getRoi() {
        float[] x = this.getXAsFloat();
        float[] y = this.getYAsFloat();
        FloatPolygon positions = new FloatPolygon(x, y, this.getSize());
        return new PolygonRoi(positions, Roi.FREELINE);
    }

    public double getPerimeter() {
        double perimeter = 0.0d;

        RealPoint point1;
        RealPoint point2;

        for (int i = 0; i < this.getSize() - 1; i++) {
            point1 = this.points.get(i);
            point2 = this.points.get(i + 1);
            perimeter += Math.hypot(
                    point2.getDoublePosition(xIndex) - point1.getDoublePosition(xIndex),
                    point2.getDoublePosition(yIndex) - point1.getDoublePosition(yIndex)
            );
        }

        return perimeter;
    }

    public final static Comparator<Contour> frameComparator = Comparator.comparing(Contour::getFrame);

    public Contour copy() {
        return new Contour(this);
    }

    private synchronized void assignID() {
        this.id = idCounter;
        idCounter++;
    }

    @Override
    public int compareTo(Contour contour) {
        return this.getId() - contour.getId();
    }

    @Override
    public String toString() {
        String out = "";
        out += "Frame: " + this.getFrame() + " | Depth: " + this.getDepth() + " | ID: " + this.getId();
        return out;
    }

    public String info() {
        String info = "";

        info += this + "\n";
        info += "N Points: " + this.numberOfPoints() + "\n";
        info += "Perimeter: " + this.getPerimeter() + "\n";
        info += "Coordinates: ";
        info += "x = " + Arrays.toString(this.getX()) + "\n";
        info += "y = " + Arrays.toString(this.getY()) + "\n";
        info += "color = " + this.getColor() + "\n";
        info += "color (hex) = " + this.getColorAsHex() + "\n";

        return info;
    }

}
