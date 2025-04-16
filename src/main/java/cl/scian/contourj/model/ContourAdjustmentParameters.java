package cl.scian.contourj.model;

import cl.scian.contourj.model.helpers.convergence.metrics.ConvergenceMetric;
import cl.scian.contourj.model.helpers.convergence.metrics.ConvergenceMetrics;
import javafx.beans.property.*;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContourAdjustmentParameters { // TODO: Add missing ASL

    private final DoubleProperty alpha = new SimpleDoubleProperty(0.5);
    private final DoubleProperty beta = new SimpleDoubleProperty(0.5);
    private final DoubleProperty gamma = new SimpleDoubleProperty(0.5);
    private final DoubleProperty kappa = new SimpleDoubleProperty(0.5);
    private final DoubleProperty mu = new SimpleDoubleProperty(0.5);
    private final DoubleProperty convergenceThreshold = new SimpleDoubleProperty(0.5);

    private final IntegerProperty iterations = new SimpleIntegerProperty(100);
    private final IntegerProperty gvfIterations = new SimpleIntegerProperty(20);

    // Flag to indicate if only internal forces should be used (no GVF/GGVF calculation)
    private final BooleanProperty useInternalForcesOnly = new SimpleBooleanProperty(false);

    private ObjectProperty<ConvergenceMetrics> convergenceMetrics = new SimpleObjectProperty<>(new ConvergenceMetrics());

    Map<String, String> parameterDisplayNames = setupParameterDisplayNames();

    private Map<String, String> setupParameterDisplayNames() {
        Map<String, String> displayNames = new HashMap<String, String>();
        displayNames.put("Elasticity", "alpha");
        displayNames.put("Rigidity", "beta");
        displayNames.put("Viscosity", "gamma");
        displayNames.put("Edge Pull Strength", "kappa");
        displayNames.put("Smoothness Control", "mu");
        displayNames.put("Vector Field Iterations", "gvfIterations");
        displayNames.put("Maximum Iterations", "iterations");
        displayNames.put("Convergence Metric", "convergenceMetric");
        displayNames.put("Convergence Threshold", "convergenceThreshold");
        return displayNames;
    }

    public double getAlpha() {
        return this.alpha.get();
    }
    public Property<Double> alphaProperty() {
        return this.alpha.asObject();
    }

    public void setAlpha(double value) {
        this.alpha.set(value);
    }

    public double getBeta() {
        return this.beta.get();
    }
    public Property<Double> betaProperty() {
        return this.beta.asObject();
    }

    public void setBeta(double value) {
        this.beta.set(value);
    }

    public double getGamma() {
        return this.gamma.get();
    }
    public Property<Double> gammaProperty() {
        return this.gamma.asObject();
    }

    public void setGamma(double value) {
        this.gamma.set(value);
    }

    public double getKappa() {
        return this.kappa.get();
    }
    public Property<Double> kappaProperty() {
        return this.kappa.asObject();
    }

    public void setKappa(double value) {
        this.kappa.set(value);
    }

    public double getMu() {
        return this.mu.get();
    }
    public Property<Double> muProperty() {
        return this.mu.asObject();
    }

    public void setMu(double value) {
        this.mu.set(value);
    }

    public int getIterations() {
        return this.iterations.get();
    }
    public Property<Integer> iterationsProperty() {
        return this.iterations.asObject();
    }

    public void setIterations(int value) {
        this.iterations.set(value);
    }

    public int getGVFIterations() {
        return this.gvfIterations.get();
    }
    public Property<Integer> gvfIterationsProperty() {
        return this.gvfIterations.asObject();
    }

    public void setGVFIterations(int value) {
        this.gvfIterations.set(value);
    }

    public boolean getUseInternalForcesOnly() {
        return this.useInternalForcesOnly.get();
    }
    
    public BooleanProperty useInternalForcesOnlyProperty() {
        return this.useInternalForcesOnly;
    }
    
    public void setUseInternalForcesOnly(boolean value) {
        this.useInternalForcesOnly.set(value);
    }

    public ConvergenceMetrics getConvergenceMetrics() {
        return this.convergenceMetrics.get();
    }
    public ObjectProperty<ConvergenceMetrics> convergenceMetricsProperty() {
        return this.convergenceMetrics;
    }

    public void setConvergenceMetrics(ConvergenceMetrics value) {
        this.convergenceMetrics.set(value);
    }

    public ConvergenceMetric getActiveConvergenceMetric() {
        return this.getConvergenceMetrics().getActiveMetric();
    }

    public double getConvergenceThreshold() {
        return this.convergenceThreshold.get();
    }
    public Property<Double> convergenceThresholdProperty() {
        return this.convergenceThreshold.asObject();
    }

    public void setConvergenceThreshold(double value) {
        this.convergenceThreshold.set(value);
    }

    public void setFromFile(File parametersFile) throws IOException {
        TomlParseResult result = Toml.parse(parametersFile.toPath());

        mapDouble(result, "Elasticity", this.alpha);
        mapDouble(result, "Rigidity", this.beta);
        mapDouble(result, "Viscosity", this.gamma);
        mapDouble(result, "Edge Pull Strength", this.kappa);
        mapDouble(result, "Smoothness Control", this.mu);
        mapDouble(result, "Convergence Threshold", this.convergenceThreshold);

        mapInteger(result, "Vector Field Iterations", this.gvfIterations);
        mapInteger(result, "Maximum Iterations", this.iterations);

        String metricName = result.getString("Convergence Metric");
        if (metricName != null) {
            this.getConvergenceMetrics().setActiveMetric(metricName); // TODO: FIX!!!
        }
    }

    public void saveToFile(File parametersFile) throws IOException {
        String tomlContent = "# ContourJ Parameters\n\n";
        
        tomlContent += "\"Elasticity\" = " + this.alpha.get() + "\n";
        tomlContent += "\"Rigidity\" = " + this.beta.get() + "\n";
        tomlContent += "\"Viscosity\" = " + this.gamma.get() + "\n";
        tomlContent += "\"Edge Pull Strength\" = " + this.kappa.get() + "\n";
        tomlContent += "\"Smoothness Control\" = " + this.mu.get() + "\n";
        tomlContent += "\"Convergence Threshold\" = " + this.convergenceThreshold.get() + "\n";
        tomlContent += "\"Vector Field Iterations\" = " + this.gvfIterations.get() + "\n";
        tomlContent += "\"Maximum Iterations\" = " + this.iterations.get() + "\n";
        tomlContent += "\"Convergence Metric\" = \"" + this.getConvergenceMetrics().getActiveMetric().getName() + "\"\n";

        try (java.io.FileWriter writer = new java.io.FileWriter(parametersFile)) {
            writer.write(tomlContent);
        }
    }

    private void mapDouble(TomlParseResult toml, String key, DoubleProperty property) {
        Double value = toml.getDouble(key);
        if (value != null) {
            property.set(value);
        }
    }

    private void mapInteger(TomlParseResult toml, String key, IntegerProperty property) {
        Long value = toml.getLong(key);
        if (value != null) {
            property.set(value.intValue());
        }
    }

    @Override
    public String toString() {
        String out = "";

        out += "alpha: " + alpha + "\n";
        out += "beta: " + beta + "\n";
        out += "gamma: " + gamma + "\n";
        out += "kappa: " + kappa + "\n";
        out += "mu: " + mu + "\n";
        out += "iterations: " + iterations + "\n";
        out += "gvf_iterations: " + gvfIterations + "\n";
        out += "convergenceMetric: " + getConvergenceMetrics().getActiveMetric() + "\n";
        out += "convergenceLimit: " + convergenceThreshold;

        return out;
    }

}

