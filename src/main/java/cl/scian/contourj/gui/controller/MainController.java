package cl.scian.contourj.gui.controller;

import cl.scian.contourj.ContourWorkflow;
import cl.scian.contourj.model.helpers.convergence.metrics.ConvergenceMetric;
import cl.scian.contourj.model.helpers.convergence.metrics.ConvergenceMetrics;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.event.ImageDisplayEvent;
import org.scijava.Context;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
// import org.tomlj.Toml;
// import org.tomlj.TomlParseResult;

// import java.io.IOException;
import java.io.File;
import java.net.URL;
// import java.nio.file.Path;
// import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @Parameter
    private Context context;

    @Parameter
    private EventService eventService;

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
    private final ImageDisplayManager imageDisplayManager;
    private final ParameterManager parameterManager;
    private final UIStateManager uiStateManager;

    public MainController(Context context, ContourWorkflow activeContours) {
        context.inject(this);
        this.contourWorkflow = activeContours;
        this.imageDisplayManager = new ImageDisplayManager(imds);
        this.parameterManager = new ParameterManager(log);
        this.uiStateManager = new UIStateManager();
        eventService.subscribe(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // this.loadConfig();
        // replaceRadioWithToggle();
    }

    public void loadPanes() {
        setupImageSelector();
        setupMaskSelector();
        setupParameters();
        setupUIBindings();
    }

    private void setupImageSelector() {
        imageDisplayManager.updateImageDisplaysList(inputSourceSelector);
        inputSourceSelector.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    contourWorkflow.setSourceDisplay(newVal);
                    imageDisplayManager.updateDatasetInfoDisplay(newVal, 
                        inputSourceTitle, inputSourceWidth, inputSourceHeight,
                        inputSourceDepth, inputSourceFrames, inputSourceChannels,
                        inputSourceBitDepth, inputSourceSource);
                }
            }
        );
        inputSourceSelector.getSelectionModel().select(imds.getActiveImageDisplay());
        inputSourceOptions.disableProperty().bind(inputRadio.selectedProperty().not());
        gvfControlPanel.disableProperty().bind(inputRadio.selectedProperty().not());
    }

    private void setupMaskSelector() {
        imageDisplayManager.updateImageDisplaysList(maskSelector);
        maskSelector.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    imageDisplayManager.updateDatasetInfoDisplay(newVal,
                        maskTitle, maskWidth, maskHeight,
                        maskDepth, maskFrames, maskChannels,
                        maskBitDepth, maskSource);
                }
            }
        );
        maskSelector.getSelectionModel().select(imds.getActiveImageDisplay());
        maskOptions.disableProperty().bind(roiRadio.selectedProperty());
    }

    private void setupParameters() {
        parameterManager.bindParameters(
            contourWorkflow.parameters(),
            alphaSpinner, betaSpinner, gammaSpinner,
            kappaSpinner, muSpinner, convergenceThresholdSpinner,
            iterationsSpinner, gvfIterationsSpinner,
            convergenceMetricSelector
        );
    }

    private void setupUIBindings() {
        uiStateManager.setupUIBindings(
            inputRadio, roiRadio,
            inputSourceOptions, maskOptions,
            annotationMethod, inputNameSummary, inputImageSelection
        );
        gvfControlPanel.disableProperty().bind(inputRadio.selectedProperty().not());
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

        //log.info("Image Display info");

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

    @FXML
    public void runAlgorithm() {
        uiStateManager.updateRunningState(true, runButton, stopButton, statusBar, statusText);
        contourWorkflow.initAdjustment();
        // When algorithm completes:
        uiStateManager.updateRunningState(false, runButton, stopButton, statusBar, statusText);
    }

    @FXML
    public void saveParameters(ActionEvent actionEvent) {
        log.info("Saving Parameters!");
    }

    @FXML
    public void loadParameters(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("Select the parameters file (.toml)", "*.toml");
        fileChooser.getExtensionFilters().add(fileExtension);
        File parametersFile = fileChooser.showOpenDialog(new Stage());
        if (parametersFile != null) {
            contourWorkflow.setParametersFromFile(parametersFile);
        } else {
            log.info("Incorrect file!");
        }
    }

    @EventHandler
    public void onEvent(ImageDisplayEvent event) {
        imageDisplayManager.updateImageDisplaysList(inputSourceSelector);
        imageDisplayManager.updateImageDisplaysList(maskSelector);
    }
}