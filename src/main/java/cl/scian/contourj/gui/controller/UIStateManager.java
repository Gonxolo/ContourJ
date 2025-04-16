package cl.scian.contourj.gui.controller;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UIStateManager {
    
    public void setupUIBindings(RadioButton inputRadio, RadioButton roiRadio,
                              VBox inputSourceOptions, VBox maskOptions,
                              Text annotationMethod, Text inputNameSummary, Text inputImageSelection) {
        
        // Bind input source options visibility to input radio selection
        inputSourceOptions.disableProperty().bind(inputRadio.selectedProperty().not());
        
        // Bind mask options visibility to ROI radio selection
        maskOptions.disableProperty().bind(roiRadio.selectedProperty());
        
        // Bind summary text elements
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
    
    public void updateRunningState(boolean isRunning, Button runButton, Button stopButton, 
                                 ProgressBar statusBar, Label statusText) {
        runButton.setVisible(!isRunning);
        stopButton.setVisible(isRunning);
        
        if (isRunning) {
            statusBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            statusText.setText("Running...");
        } else {
            statusBar.setProgress(0.0);
            statusText.setText("Run completed successfully! Now ready to run again.");
        }
    }
} 