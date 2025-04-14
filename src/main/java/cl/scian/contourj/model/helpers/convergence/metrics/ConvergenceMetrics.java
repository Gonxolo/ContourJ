package cl.scian.contourj.model.helpers.convergence.metrics;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ConvergenceMetrics {

    private final ArrayList<ConvergenceMetric> convergenceMetrics;
    private ConvergenceMetric activeMetric;

    public ConvergenceMetrics() {
        this.convergenceMetrics = new ArrayList<ConvergenceMetric>();
        this.convergenceMetrics.add(new AverageDistance());
        this.convergenceMetrics.add(new AverageFractionPerimeter());
        this.convergenceMetrics.add(new HausdorffDistance());
        this.convergenceMetrics.add(new L1Norm());
        this.convergenceMetrics.add(new L2Norm());
        this.convergenceMetrics.add(new LInfNorm());
        this.activeMetric = this.convergenceMetrics.get(0);
    }

    public ConvergenceMetric getActiveMetric() {
        return this.activeMetric;
    }

    public void setActiveMetric(int index) {
        try {
            this.activeMetric = this.convergenceMetrics.get(index);
        } catch (IndexOutOfBoundsException e) {
            return;
        }
    }

    public void setActiveMetric(String name) {
        for (ConvergenceMetric convergenceMetric : this.convergenceMetrics) {
            if (convergenceMetric.getName().equalsIgnoreCase(name)) {
                this.activeMetric = convergenceMetric;
                return;
            }
        }
    }

    public ObservableList<ConvergenceMetric> getObservableMetrics() {
        return FXCollections.observableList(this.convergenceMetrics);
    }

    @Override
    public String toString(){
        String out = "[ ";
        for (int i = 0; i < this.convergenceMetrics.size() - 1; i++) {
            out += i + ": " + this.convergenceMetrics.get(i).getName() + " ";
        }
        out += " ]";
        return out;
    }

}
