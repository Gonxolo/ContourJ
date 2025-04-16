package cl.scian.contourj.gui.controller;

import cl.scian.contourj.model.ContourAdjustmentParameters;
import cl.scian.contourj.model.helpers.convergence.metrics.ConvergenceMetric;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;
import org.scijava.log.LogService;

import java.text.DecimalFormat;
import java.text.ParseException;

public class ParameterManager {
    private final LogService log;

    public ParameterManager(LogService log) {
        this.log = log;
    }

    public void bindParameters(ContourAdjustmentParameters params,
                             Spinner<Double> alphaSpinner,
                             Spinner<Double> betaSpinner,
                             Spinner<Double> gammaSpinner,
                             Spinner<Double> kappaSpinner,
                             Spinner<Double> muSpinner,
                             Spinner<Double> convergenceThresholdSpinner,
                             Spinner<Integer> iterationsSpinner,
                             Spinner<Integer> gvfIterationsSpinner,
                             ComboBox<ConvergenceMetric> convergenceMetricSelector) {
        
        // Alpha
        configureDoubleSpinner(alphaSpinner, 1e-4, 10.0, params.getAlpha(), 1e-4);
        alphaSpinner.getValueFactory().valueProperty().bindBidirectional(params.alphaProperty());

        // Beta
        configureDoubleSpinner(betaSpinner, 1e-4, 10.0, params.getBeta(), 1e-4);
        betaSpinner.getValueFactory().valueProperty().bindBidirectional(params.betaProperty());

        // Gamma
        configureDoubleSpinner(gammaSpinner, 1e-4, 10.0, params.getGamma(), 1e-4);
        gammaSpinner.getValueFactory().valueProperty().bindBidirectional(params.gammaProperty());

        // Kappa
        configureDoubleSpinner(kappaSpinner, 0.0, 10.0, params.getKappa(), 1e-4);
        kappaSpinner.getValueFactory().valueProperty().bindBidirectional(params.kappaProperty());

        // Mu
        configureDoubleSpinner(muSpinner, 1e-4, Double.MAX_VALUE, params.getMu(), 1e-1);
        muSpinner.getValueFactory().valueProperty().bindBidirectional(params.muProperty());

        // Convergence Threshold
        configureDoubleSpinner(convergenceThresholdSpinner, 1e-2, Double.MAX_VALUE, params.getConvergenceThreshold(), 1e-1);
        convergenceThresholdSpinner.getValueFactory().valueProperty().bindBidirectional(params.convergenceThresholdProperty());

        // Iterations
        configureIntegerSpinner(iterationsSpinner, 1, (int) 1e5, params.getIterations(), 100);
        iterationsSpinner.getValueFactory().valueProperty().bindBidirectional(params.iterationsProperty());

        // GVF Iterations
        configureIntegerSpinner(gvfIterationsSpinner, 1, (int) 1e3, params.getGVFIterations(), 10);
        gvfIterationsSpinner.getValueFactory().valueProperty().bindBidirectional(params.gvfIterationsProperty());

        // Convergence Metric
        convergenceMetricSelector.setItems(params.getConvergenceMetrics().getObservableMetrics());
        convergenceMetricSelector.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> params.getConvergenceMetrics().setActiveMetric(params.getConvergenceMetrics().getObservableMetrics().indexOf(newVal))
        );
        convergenceMetricSelector.getSelectionModel().selectFirst();
    }

    private void configureDoubleSpinner(Spinner<Double> spinner, double min, double max, double initialValue, double step) {
        SpinnerValueFactory.DoubleSpinnerValueFactory factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, step);

        factory.setConverter(new DoubleStringConverter() {
            private final DecimalFormat format = new DecimalFormat("0.0###") {
                {
                    setMaximumFractionDigits(4);
                    setMinimumIntegerDigits(1);
                }
            };

            @Override
            public String toString(Double value) {
                if (value == null) return String.valueOf(min);
                return format.format(value);
            }

            @Override
            public Double fromString(String string) {
                if (string == null || string.trim().isEmpty()) return min;
                try {
                    double value = format.parse(string).doubleValue();
                    return Math.max(min, Math.min(max, value));
                } catch (ParseException e) {
                    log.debug("Error parsing double value: " + e.getMessage());
                    return initialValue;
                }
            }
        });

        spinner.setValueFactory(factory);

        TextFormatter<Double> textFormatter = new TextFormatter<>(factory.getConverter(), initialValue, change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*\\.?\\d*")) {
                return change;
            }
            return null;
        });

        spinner.getEditor().setTextFormatter(textFormatter);
        enableImmediateSpinnerUpdates(spinner);
    }

    private void configureIntegerSpinner(Spinner<Integer> spinner, int min, int max, int initialValue, int step) {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue, step);
        spinner.setValueFactory(factory);

        TextFormatter<Integer> textFormatter = new TextFormatter<>(factory.getConverter(), initialValue, change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d+")) {
                return change;
            }
            return null;
        });

        spinner.getEditor().setTextFormatter(textFormatter);
        enableImmediateSpinnerUpdates(spinner);
    }

    private <T> void enableImmediateSpinnerUpdates(Spinner<T> spinner) {
        spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                String formattedValue = spinner.getValueFactory().getConverter().fromString(newValue).toString();
                spinner.getValueFactory().setValue(
                        spinner.getValueFactory().getConverter().fromString(formattedValue)
                );
            } catch (NumberFormatException e) {
                spinner.getEditor().setText(oldValue);
                log.debug("Invalid number format: " + e.getMessage());
            } catch (Exception e) {
                log.error("Error in spinner update: " + e.getMessage());
                spinner.getEditor().setText(oldValue);
            }
        });
    }
} 