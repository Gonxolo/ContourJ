package cl.scian.contourj.gui.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UIStateManager {
    
    private StringProperty inputNameProperty = new SimpleStringProperty("<no-image-selected>");
    private StringProperty maskNameProperty = new SimpleStringProperty("<no-mask-selected>");
    
    public void setupUIBindings(RadioButton inputRadio, RadioButton roiRadio,
                              VBox inputSourceOptions, VBox maskOptions,
                              Text annotationMethod, Text inputNameSummary, Text inputImageSelection) {
        
        // Bind input source options visibility to input radio selection
        inputSourceOptions.disableProperty().bind(inputRadio.selectedProperty().not());
        
        // Bind mask options visibility to ROI manager selection
        maskOptions.disableProperty().bind(roiRadio.selectedProperty());
        
        // Bind summary text elements
        annotationMethod.textProperty().bind(
            Bindings.createStringBinding(() ->
                roiRadio.isSelected() ? "from ROI Manager" : "from Mask Image " + maskNameProperty.get(),
                roiRadio.selectedProperty(), maskNameProperty
            )
        );
        
        // Bind to the appropriate property based on selection
        inputNameSummary.textProperty().bind(
            Bindings.createStringBinding(() -> {
                if (inputRadio.isSelected()) {
                    return inputNameProperty.get();
                } else {
                    return "with no image associated";
                }
            }, inputRadio.selectedProperty(), roiRadio.selectedProperty(), 
               inputNameProperty, maskNameProperty)
        );
        
        inputImageSelection.textProperty().bind(
            Bindings.createStringBinding(() ->
                inputRadio.isSelected() ? " will be processed with the image " : " will be processed ",
                inputRadio.selectedProperty()
            )
        );
    }
    
    public void setupOutputOptionsSummary(Text outputOptionsSummary,
                                      CheckBox preserveOriginalInputCheckbox,
                                      CheckBox addToRoiManagerCheckbox,
                                      CheckBox preserveOriginalAnnotationsCheckbox,
                                      CheckBox imageWithOverlayCheckbox,
                                      CheckBox exportRoiZipCheckbox,
                                      CheckBox exportCsvCheckbox,
                                      CheckBox exportTxtCheckbox) {
        
        outputOptionsSummary.textProperty().bind(
            Bindings.createStringBinding(() -> {
                StringBuilder sb = new StringBuilder();
                if (preserveOriginalInputCheckbox.isSelected()) sb.append("Keep original source, ");
                if (imageWithOverlayCheckbox.isSelected()) sb.append("Image with overlay, ");
                if (addToRoiManagerCheckbox.isSelected()) sb.append("Add to ROI Manager, ");
                if (preserveOriginalAnnotationsCheckbox.isSelected()) sb.append("Keep original annotations, ");
                if (exportRoiZipCheckbox.isSelected()) sb.append("Export to .roi/.zip, ");
                if (exportCsvCheckbox.isSelected()) sb.append("Export points (.csv), ");
                if (exportTxtCheckbox.isSelected()) sb.append("Export points (.txt)");
                
                return sb.toString();
            }, preserveOriginalInputCheckbox.selectedProperty(),
               addToRoiManagerCheckbox.selectedProperty(),
               preserveOriginalAnnotationsCheckbox.selectedProperty(),
               imageWithOverlayCheckbox.selectedProperty(),
               exportRoiZipCheckbox.selectedProperty(),
               exportCsvCheckbox.selectedProperty(),
               exportTxtCheckbox.selectedProperty())
        );
    }
    
    public void updateInputName(String name) {
        System.out.println("input name: " + name);
        // Always use the placeholder text when name is null or empty
        if (name == null || name.isEmpty()) {
            inputNameProperty.set("<no-image-selected>");
        } else {
            inputNameProperty.set(name);
        }
    }
    
    public void updateMaskName(String name) {
        System.out.println("mask name: " + name);
        // Always use the placeholder text when name is null or empty
        if (name == null || name.isEmpty()) {
            maskNameProperty.set("<no-mask-selected>");
        } else {
            maskNameProperty.set(name);
        }
    }
    
    // Sets the UI elements to the running state
    public void updateRunningState(boolean isRunning, Button runButton, Button stopButton, 
                                ProgressBar statusBar, Label statusText) {
        runButton.setDisable(isRunning);
        stopButton.setDisable(!isRunning);
        statusBar.setProgress(isRunning ? -1 : 0);
        statusBar.setVisible(isRunning);
        statusText.setText(isRunning ? "Running..." : "Ready");
    }
} 