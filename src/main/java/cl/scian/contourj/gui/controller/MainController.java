package cl.scian.contourj.gui.controller;

import cl.scian.contourj.ContourWorkflow;
import cl.scian.contourj.model.helpers.convergence.metrics.ConvergenceMetric;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import org.scijava.Context;
import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
// import org.tomlj.Toml;
// import org.tomlj.TomlParseResult;

import ij.plugin.frame.RoiManager;

// import java.io.IOException;
import java.io.File;
import java.io.IOException;
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
    private CheckBox preserveOriginalInputCheckbox;
    @FXML
    private CheckBox addToRoiManagerCheckbox;
    @FXML
    private CheckBox preserveOriginalAnnotationsCheckbox;
    @FXML
    private CheckBox imageWithOverlayCheckbox;
    @FXML
    private CheckBox exportRoiZipCheckbox;
    @FXML
    private CheckBox exportCsvCheckbox;
    @FXML
    private CheckBox exportTxtCheckbox;
    @FXML
    private ProgressBar statusBar;
    @FXML
    private Label statusText;
    @FXML
    private Button runButton;
    @FXML
    private StackPane gvfControlPanel;

    private final ContourWorkflow contourWorkflow;
    private ImageDisplayManager imageDisplayManager;
    private final ParameterManager parameterManager;
    private final UIStateManager uiStateManager;

    public MainController(Context context, ContourWorkflow activeContours) {
        context.inject(this);
        this.contourWorkflow = activeContours;
        this.parameterManager = new ParameterManager(log);
        this.uiStateManager = new UIStateManager();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize will be called by JavaFX after FXML fields are injected
    }

    public void loadPanes() {
        // Create the image display manager now that the UI components exist
        this.imageDisplayManager = new ImageDisplayManager(imds, inputSourceSelector, maskSelector);
        context.inject(this.imageDisplayManager);
        eventService.subscribe(this.imageDisplayManager);
        this.imageDisplayManager.initialize();
        
        setupImageSelector();
        setupMaskSelector();
        setupParameters();
        setupUIBindings();
        
        // Force an update of the image displays after setting up
        log.info("Forcing initial update of image displays");
        imageDisplayManager.updateAllComboBoxes();
    }

    private void setupImageSelector() {
        inputSourceSelector.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                log.info("Image selection changed: " + (newVal != null ? newVal.getName() : "null"));
                if (newVal != null) {
                    contourWorkflow.setSourceDisplay(newVal);
                    imageDisplayManager.updateDatasetInfoDisplay(newVal, 
                        inputSourceTitle, inputSourceWidth, inputSourceHeight,
                        inputSourceDepth, inputSourceFrames, inputSourceChannels,
                        inputSourceBitDepth, inputSourceSource);
                    
                    // Update input name in the summary
                    uiStateManager.updateInputName(newVal.getName());
                } else {
                    // When no image is selected
                    uiStateManager.updateInputName(null);
                }
            }
        );
        
        // Only select active display if there is one
        ImageDisplay activeDisplay = imds.getActiveImageDisplay();
        if (activeDisplay != null) {
            inputSourceSelector.getSelectionModel().select(activeDisplay);
        } else {
            // Make sure nothing is selected to show placeholder
            inputSourceSelector.getSelectionModel().clearSelection();
            uiStateManager.updateInputName(null);
        }

        gvfControlPanel.disableProperty().bind(inputRadio.selectedProperty().not());
    }

    private void setupMaskSelector() {
        maskSelector.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                log.info("Mask selection changed: " + (newVal != null ? newVal.getName() : "null"));
                if (newVal != null) {
                    imageDisplayManager.updateDatasetInfoDisplay(newVal,
                        maskTitle, maskWidth, maskHeight,
                        maskDepth, maskFrames, maskChannels,
                        maskBitDepth, maskSource);
                    
                    // Update mask name in the summary
                    uiStateManager.updateMaskName(newVal.getName());
                } else {
                    // When no mask is selected
                    uiStateManager.updateMaskName(null);
                }
            }
        );
        
        // Only select active display if there is one
        ImageDisplay activeDisplay = imds.getActiveImageDisplay();
        if (activeDisplay != null) {
            maskSelector.getSelectionModel().select(activeDisplay);
        } else {
            // Make sure nothing is selected to show placeholder
            maskSelector.getSelectionModel().clearSelection();
            uiStateManager.updateMaskName(null);
        }
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
            inputSourceOptions, maskOptions
        );
        
        // Bind the useInternalForcesOnly property to the inverse of inputRadio.selectedProperty
        // When inputRadio is NOT selected, we're using internal forces only
        contourWorkflow.parameters().useInternalForcesOnlyProperty().bind(inputRadio.selectedProperty().not());
        
        // Disable GVF panel when using internal forces only
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

    @FXML
    public void runAlgorithm() {
        // Validate requirements before running
        if (!validateRequirements()) {
            return; // Stop execution if validation fails
        }
        
        try {
            // Disable image display updates during processing
            imageDisplayManager.setUpdatesEnabled(false);
            
            // Update UI to show running state
            uiStateManager.updateRunningState(true, runButton, statusBar, statusText);
            
            // Set workflow options from UI
            contourWorkflow.setPreserveOriginalAnnotations(preserveOriginalAnnotationsCheckbox.isSelected());
            
            // Run the adjustment
            contourWorkflow.initAdjustment();
            
        } finally {
            // Re-enable updates when done (even if an exception occurs)
            imageDisplayManager.setUpdatesEnabled(true);
            
            // Reset UI state
            uiStateManager.updateRunningState(false, runButton, statusBar, statusText);
        }
    }
    
    private boolean validateRequirements() {
        // Check if ROI method is selected but ROI Manager is empty
        if (roiRadio.isSelected()) {
            RoiManager rm = RoiManager.getInstance();
            if (rm == null || rm.getRoisAsArray().length == 0) {
                showAlert("ROI Manager is empty", 
                    "You selected to use ROIs from the ROI Manager, but it's empty.\n" +
                    "Please add ROIs to the ROI Manager before running the algorithm.");
                return false;
            }
        }
        
        // Check if mask method is selected but no mask image is given
        if (!roiRadio.isSelected() && (maskSelector.getSelectionModel().isEmpty() || 
                                       maskSelector.getSelectionModel().getSelectedItem() == null)) {
            showAlert("No mask selected", 
                "You selected to use a mask, but no mask image is selected.\n" +
                "Please select a mask image before running the algorithm.");
            return false;
        }
        
        // Check if internal and external forces are selected but no input source is given
        if (inputRadio.isSelected() && (inputSourceSelector.getSelectionModel().isEmpty() || 
                                        inputSourceSelector.getSelectionModel().getSelectedItem() == null)) {
            showAlert("No input source selected", 
                "You selected to use internal and external forces, but no input image is selected.\n" +
                "Please select an input image before running the algorithm.");
            return false;
        }
        
        return true;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void saveParameters(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("Save parameters file (.toml)", "*.toml");
        fileChooser.getExtensionFilters().add(fileExtension);
        fileChooser.setInitialFileName("contourj_parameters.toml");
        File parametersFile = fileChooser.showSaveDialog(new Stage());
        if (parametersFile != null) {
            try {
                contourWorkflow.saveParametersToFile(parametersFile);
                log.info("Parameters successfully saved to: " + parametersFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("Error saving parameters: " + e.getMessage());
                log.warn(e);
            }
        }
    }

    @FXML
    public void loadParameters(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtension = new FileChooser.ExtensionFilter("Select the parameters file (.toml)", "*.toml");
        fileChooser.getExtensionFilters().add(fileExtension);
        File parametersFile = fileChooser.showOpenDialog(new Stage());
        if (parametersFile != null) {
            try {
                contourWorkflow.setParametersFromFile(parametersFile);
                log.info("Parameters successfully loaded from: " + parametersFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("Error loading parameters: " + e.getMessage());
                log.warn(e);
            }
        }
    }
}