package cl.scian.contourj.model;

import java.util.ArrayList;
import java.util.Iterator;

public class Contours implements Iterable<Contour> {

    private ArrayList<Contour> contours;

    public Contours(int size) {
        this.contours = new ArrayList<Contour>(size);
    }

    public Contours(Contours contours){
        this.contours = new ArrayList<Contour>(contours.size());
        for (Contour contour : contours) {
            this.add(new Contour(contour));
        }
    }

    public int size() {
        return this.contours.size();
    }

    public Contours copy() {
        return new Contours(this);
    }

    @Override
    public String toString() {
        String out = "";
        for (Contour contour : this) {
            // TODO: Profile str concat to see if StringBuilder is needed (readability vs. performance)
            out += contour.toString() + "\n";
        }
        return out;
    }

    public String info() {
        String out = "";
        for (Contour contour : this) {
            out += contour.info() + "\n";
        }
        return out;
    }

    @Override
    public Iterator<Contour> iterator() {
        return this.contours.iterator();
    }

    public boolean add(Contour contour) {
        return this.contours.add(contour);
    }

    public Contour get(int index) {
        return this.contours.get(index);
    }

}
