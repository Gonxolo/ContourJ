package cl.scian.contourj.gui.controller;


import cl.scian.contourj.ContourWorkflow;

import cl.scian.contourj.model.ContourAdjustmentParameters;
import cl.scian.contourj.model.helpers.convergence.metrics.ConvergenceMetric;
import cl.scian.contourj.model.helpers.convergence.metrics.ConvergenceMetrics;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.concurrent.Task;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
// import org.tomlj.Toml;
// import org.tomlj.TomlParseResult;

// import java.io.IOException;
import java.io.File;
import java.net.URL;
// import java.nio.file.Path;
// import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @Parameter
    private Context context;

    @Parameter
    private LogService log;

    @Parameter
    private ImageDisplayService imds;

    @FXML
    private ComboBox<ImageDisplay> inputSourceSelector;
    @FXML
    private Label inputSourceTitle;
    @FXML
    private Label inputSourceWidth;
    @FXML
    private Label inputSourceHeight;
    @FXML
    private Label inputSourceDepth;
    @FXML
    private Label inputSourceFrames;
    @FXML
    private Label inputSourceChannels;
    @FXML
    private Label inputSourceBitDepth;
    @FXML
    private Label inputSourceSource;
    @FXML
    private VBox inputSourceOptions;
    @FXML
    private RadioButton inputRadio;
    @FXML
    private ComboBox<ImageDisplay> maskSelector;
    @FXML
    private Label maskTitle;
    @FXML
    private Label maskWidth;
    @FXML
    private Label maskHeight;
    @FXML
    private Label maskDepth;
    @FXML
    private Label maskFrames;
    @FXML
    private Label maskChannels;
    @FXML
    private Label maskBitDepth;
    @FXML
    private Label maskSource;
    @FXML
    private VBox maskOptions;
    @FXML
    private RadioButton roiRadio;
    @FXML
    private Spinner<Double> alphaSpinner;
    @FXML
    private Spinner<Double> betaSpinner;
    @FXML
    private Spinner<Double> gammaSpinner;
    @FXML
    private Spinner<Double> kappaSpinner;
    @FXML
    private Spinner<Double> muSpinner;
    @FXML
    private Spinner<Double> convergenceThresholdSpinner;
    @FXML
    private Spinner<Integer> iterationsSpinner;
    @FXML
    private Spinner<Integer> gvfIterationsSpinner;
    @FXML
    private ComboBox<ConvergenceMetric> convergenceMetricSelector;
    @FXML
    private Text annotationMethod;
    @FXML
    private Text inputImageSelection;
    @FXML
    private Text inputNameSummary;
    @FXML
    private ProgressBar statusBar;
    @FXML
    private Label statusText;
    @FXML
    private Button stopButton;
    @FXML
    private Button runButton;
    @FXML
    private StackPane gvfControlPanel;

    private final ContourWorkflow contourWorkflow;

    public MainController(Context context, ContourWorkflow activeContours) {
        context.inject(this);
        this.contourWorkflow = activeContours;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // this.loadConfig();
        // replaceRadioWithToggle();
    }

    public void loadPanes() {
        setupImageSelector();
        setupMaskSelector();
        bindSpinnersToParameters();
        setupConvergenceMetricSelector();
        setupSummary();
    }

    private void setupImageSelector() {
        // Initialize the ComboBox with current ImageDisplays
        updateImageDisplaysList(inputSourceSelector);

        inputSourceSelector.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                updateImageDisplaysList(inputSourceSelector);
            }
        });

        inputSourceSelector.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        contourWorkflow.setSourceDisplay(newVal);
                        updateSourceInfoDisplay();
                        // log.info("Source Image: " + contourWorkflow.getSourceDisplay());
                    }
                }
        );
        inputSourceSelector.getSelectionModel().select(imds.getActiveImageDisplay());
        inputSourceOptions.disableProperty().bind(inputRadio.selectedProperty().not());
        gvfControlPanel.disableProperty().bind(inputRadio.selectedProperty().not());
    }

    /**
     * Sets up the display of details of the selected image
     */
    private void updateSourceInfoDisplay() {
        // Retrieve active image
        Dataset dataset = imds.getActiveDataset(inputSourceSelector.getSelectionModel().getSelectedItem());

        // Get and set the name
        inputSourceTitle.setText(dataset.getName());

        // TODO: Consider calibration
        // CalibratedAxis X = dataset.axis(0);
        // CalibratedAxis Y = dataset.axis(1);
        // CalibratedAxis Z = dataset.axis(2);
        //dimensionText += X.unit() + "[" + X.type() + "]" + ", " + Y.unit() + "[" + Y.type() + "]";

        // Get and set the width (x) in pixels
        inputSourceWidth.setText(dataset.getWidth() + " [px]");

        // Get and set the height (y) in pixels
        inputSourceHeight.setText(dataset.getHeight() + " [px]");

        // Get and set the depth (z)
        inputSourceDepth.setText(String.valueOf(dataset.getDepth()));

        // Get and set the frames (t)
        inputSourceFrames.setText(String.valueOf(dataset.getFrames()));

        // Get and set the channels (s)
        inputSourceChannels.setText(String.valueOf(dataset.getChannels()));

        // Get and set the bit depth
        inputSourceBitDepth.setText(dataset.getTypeLabelLong());

        // Get and set the source
        inputSourceSource.setText(dataset.getSource());
    }

    private void setupMaskSelector() {
        // Initialize the ComboBox with current ImageDisplays
        updateImageDisplaysList(maskSelector);
        maskSelector.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (isShowing) {
                updateImageDisplaysList(maskSelector);
            }
        });

        maskSelector.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        // activeContours.setInitialContours(); // TODO: Pass mask to the setContours method
                        updateMaskInfoDisplay();
                        // log.info("Initial contours: " + contourWorkflow.getInitialContours());
                    }
                }
        );
        maskSelector.getSelectionModel().select(imds.getActiveImageDisplay());
        maskOptions.disableProperty().bind(roiRadio.selectedProperty());
    }

    /**
     * Sets up the display of details of the selected mask
     */
    private void updateMaskInfoDisplay() {
        // Retrieve active image
        Dataset dataset = imds.getActiveDataset(maskSelector.getSelectionModel().getSelectedItem());

        // Get and set the name
        maskTitle.setText(dataset.getName());

        // TODO: Consider calibration
        // CalibratedAxis X = dataset.axis(0);
        // CalibratedAxis Y = dataset.axis(1);
        // CalibratedAxis Z = dataset.axis(2);
        //dimensionText += X.unit() + "[" + X.type() + "]" + ", " + Y.unit() + "[" + Y.type() + "]";

        // Get and set the width (x) in pixels
        maskWidth.setText(dataset.getWidth() + " [px]");

        // Get and set the height (y) in pixels
        maskHeight.setText(dataset.getHeight() + " [px]");

        // Get and set the depth (z)
        maskDepth.setText(String.valueOf(dataset.getDepth()));

        // Get and set the frames (t)
        maskFrames.setText(String.valueOf(dataset.getFrames()));

        // Get and set the channels (s)
        maskChannels.setText(String.valueOf(dataset.getChannels()));

        // Get and set the bit depth
        maskBitDepth.setText(dataset.getTypeLabelLong());

        // Get and set the source
        maskSource.setText(dataset.getSource());
    }

    private void updateImageDisplaysList(ComboBox<ImageDisplay> comboBox) {
        Platform.runLater(() -> {
            comboBox.setItems(FXCollections.observableList(imds.getImageDisplays()));
            // log.info("Image displays: " + imds.getImageDisplays()); // TODO: Remove log
        });
    }

    // TODO: fix decimal precision
    // TODO: fix number input (change without pressing enter)
    // TODO: refactor this method (specifically the max, min, step and default values should be elsewhere)
    private void bindSpinnersToParameters() {
        ContourAdjustmentParameters params = this.contourWorkflow.parameters();

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

        // Iter
        configureIntegerSpinner(iterationsSpinner, 1, (int) 1e5, params.getIterations(), 100);
        iterationsSpinner.getValueFactory().valueProperty().bindBidirectional(params.iterationsProperty());

        // GVFIter
        configureIntegerSpinner(gvfIterationsSpinner, 1, (int) 1e3, params.getGVFIterations(), 10);
        gvfIterationsSpinner.getValueFactory().valueProperty().bindBidirectional(params.gvfIterationsProperty());

        // ConvThresh
        configureDoubleSpinner(convergenceThresholdSpinner, 1e-2, Double.MAX_VALUE, params.getConvergenceThreshold(), 1e-1);
        convergenceThresholdSpinner.getValueFactory().valueProperty().bindBidirectional(params.convergenceThresholdProperty());
    }

    private void configureDoubleSpinner(Spinner<Double> spinner, double min, double max, double initialValue, double step) {

        SpinnerValueFactory.DoubleSpinnerValueFactory factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, step);

        // Set decimal format
        factory.setConverter(new DoubleStringConverter() {
            private final DecimalFormat format = new DecimalFormat("0.0###") {
                @Override
                public void setMaximumFractionDigits(int newValue) {
                    super.setMaximumFractionDigits(4);
                }

                @Override
                public void setMinimumIntegerDigits(int newValue) {
                    super.setMinimumIntegerDigits(1);
                }
            };

            @Override
            public String toString(Double value) {
                return format.format(value);
            }

            @Override
            public Double fromString(String string) {
                try {
                    return format.parse(string).doubleValue();
                } catch (ParseException e) {
                    return 0.0;
                }
            }
        });

        spinner.setValueFactory(factory);

        TextFormatter<Double> textFormatter = new TextFormatter<>(factory.getConverter(), initialValue, change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d+\\.\\d+")) {
                return change;
            }
            return null; // Reject the change if it's not a valid integer
        });

        spinner.getEditor().setTextFormatter(textFormatter);

        // Enable immediate updates on typing
        enableImmediateSpinnerUpdates(spinner);
    }

    private void configureIntegerSpinner(Spinner<Integer> spinner, int min, int max,
                                         int initialValue, int step) {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue, step);
        spinner.setValueFactory(factory);

        // Add a TextFormatter to filter out non-numeric characters
        TextFormatter<Integer> textFormatter = new TextFormatter<>(factory.getConverter(), initialValue, change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d+")) { // Allow only digits
                return change;
            }
            return null; // Reject the change if it's not a valid integer
        });

        spinner.getEditor().setTextFormatter(textFormatter);

        // Enable immediate updates on typing
        enableImmediateSpinnerUpdates(spinner);
    }

    private <T> void enableImmediateSpinnerUpdates(Spinner<T> spinner) {
        spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            System.out.println(obs);
            System.out.println(oldValue);
            System.out.println(newValue);
            try {
                String formattedValue = spinner.getValueFactory().getConverter().fromString(newValue).toString();
                spinner.getValueFactory().setValue(
                        spinner.getValueFactory().getConverter().fromString(formattedValue)
                );
            } catch (NumberFormatException e) {
                // Ignore invalid input
            } catch (Exception e) {
                System.out.println("Error in immediate spinner update");
                e.printStackTrace();
            }
        });
    }

    private void setupConvergenceMetricSelector() {

        ConvergenceMetrics metrics = this.contourWorkflow.parameters().getConvergenceMetrics();
        convergenceMetricSelector.setItems(metrics.getObservableMetrics());

        // Bind selection to active metric
        convergenceMetricSelector.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> metrics.setActiveMetric(metrics.getObservableMetrics().indexOf(newVal))
        );
        convergenceMetricSelector.getSelectionModel().selectFirst();
    }

//    private void loadConfig() {
//        try {
//            Path source = Paths.get("/config.toml");
//            TomlParseResult result = Toml.parse(source);
//            result.errors().forEach(error -> System.err.println(error.toString()));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private void setupSummary() {
        annotationMethod.textProperty().bind(
                Bindings.createStringBinding(() ->
                        roiRadio.isSelected() ? "from ROI Manager" : "from Mask Image",
                        roiRadio.selectedProperty()
                )
        );
        inputNameSummary.textProperty().bind(
                Bindings.createStringBinding(() ->
                                inputRadio.isSelected() ? "getInputName()" : "(with no image associated)",
                        inputRadio.selectedProperty()
                )
        );
        inputImageSelection.textProperty().bind(
                Bindings.createStringBinding(() ->
                                inputRadio.isSelected() ? " will be processed for the image " : " will be processed ",
                        inputRadio.selectedProperty()
                )
        );
    }

    private String datasetInfo(Dataset dataset, String datasetName) {
        String out = "";
        out += "--- " + datasetName + " INFO ---" + "\n";
        out += "Title: " + dataset.getName() + "\n";
        out += "Width (X): " + dataset.getWidth() + "\n";
        out += "Height (Y): " + dataset.getHeight() + "\n";
        out += "Depth (Z): " + dataset.getDepth() + "\n";
        out += "Frames (T): " + dataset.getFrames() + "\n";
        out += "Channels (C): " + dataset.getChannels() + "\n";
        out += "Type Label: " + dataset.getTypeLabelLong() + "\n";
        out += "Source: " + dataset.getSource() + "\n";
        out += "--- %%%%%%%%%%%%%%%%%%% ---\n";
        return out;
    }

    private void displayImageInfo() {
        Dataset maskDataset = null;
        Dataset inputSourceDataset = null;

        log.info("Image Display info");

        try {
            maskDataset = imds.getActiveDataset(maskSelector.getSelectionModel().getSelectedItem());
        } catch (NullPointerException e) {
            log.error("Mask Error");
            log.error(e);
        }

        try {
            inputSourceDataset = imds.getActiveDataset(inputSourceSelector.getSelectionModel().getSelectedItem());
        } catch (NullPointerException e) {
            log.error("Input Source Error");
            log.error(e);
        }

        if (maskDataset != null) {
            log.info(this.datasetInfo(maskDataset, "MASK"));
        } else {
            log.warn("No mask dataset");
        }

        if (inputSourceDataset != null) {
            log.info(this.datasetInfo(inputSourceDataset, "INPUT SOURCE"));
        } else {
            log.warn("No input source dataset");
        }

    }

    // TODO: runAlgorithm probably should trigger some events
    @FXML
    public void runAlgorithm() {
        // TODO: Disable Run button (probably should also disable all the other controls (except the summary)
        // Enable Pause and Stop buttons
        runButton.setVisible(false);
        stopButton.setVisible(true);

        statusBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        statusText.setText("Running...");

        displayImageInfo();

        this.contourWorkflow.initAdjustment();

        delay(2000, this::algorithmDone); // TODO: Remove delay

    }


    // TODO: algorithmDone probably should be triggered by an event
    private void algorithmDone() {
        runButton.setVisible(true);
        stopButton.setVisible(false);

        statusBar.setProgress(0.0d);
        statusText.setText("Run completed successfully! Now ready to run again.");
    }
    private static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() {
                try { Thread.sleep(millis); }
                catch (InterruptedException ignored) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }

    @FXML
    public void saveParameters(ActionEvent actionEvent) {
        log.info("Saving Parameters!");
    }

    @FXML
    public void loadParameters(ActionEvent actionEvent) {
        log.info("Loading Parameters!"); // TODO: Add to logs.
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("Select the parameters file (.toml)", "*.toml");
        fileChooser.getExtensionFilters().add(fileExtension);
        File parametersFile = fileChooser.showOpenDialog(new Stage());
        if (parametersFile != null) {
            contourWorkflow.setParametersFromFile(parametersFile);
        } else {
            log.info("Incorrect file!"); // TODO: Add to logs.
        }
    }

}