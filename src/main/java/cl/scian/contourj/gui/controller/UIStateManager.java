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
    
    public void updateInputName(String name) {
        System.out.println("input name: " + name);
        inputNameProperty.set((name != null) && (!name.isEmpty()) ? name : "<no-image-selected>");
    }
    
    public void updateMaskName(String name) {
        System.out.println("mask name: " + name);
        maskNameProperty.set((name != null) && (!name.isEmpty()) ? name : "<no-mask-selected>");
    }
    
    public void setupOutputOptionsSummary(
            Text outputOptionsSummary,
            CheckBox preserveOriginalInputCheckbox, 
            CheckBox addToRoiManagerCheckbox,
            CheckBox preserveOriginalAnnotationsCheckbox,
            CheckBox imageWithOverlayCheckbox,
            CheckBox exportRoiZipCheckbox,
            CheckBox exportCsvCheckbox,
            CheckBox exportTxtCheckbox) {
        
        // Create binding for output options summary
        StringProperty outputSummaryProperty = new SimpleStringProperty("");
        
        // Update the summary whenever any checkbox changes
        Runnable updateSummary = () -> {
            StringBuilder sb = new StringBuilder();
            
            // Add main output options
            if (preserveOriginalInputCheckbox.isSelected()) {
                appendWithComma(sb, "original input source will be preserved");
            }
            
            if (addToRoiManagerCheckbox.isSelected()) {
                appendWithComma(sb, "resulting contours will be added to the ROI Manager");
            }
            
            if (preserveOriginalAnnotationsCheckbox.isSelected()) {
                appendWithComma(sb, "original annotations will be preserved");
            }
            
            if (imageWithOverlayCheckbox.isSelected()) {
                appendWithComma(sb, "an image with overlay of the results will be generated");
            }
            
            // Add export options if any are selected
            if (exportRoiZipCheckbox.isSelected() || exportCsvCheckbox.isSelected() || exportTxtCheckbox.isSelected()) {
                String exportFormats = getExportFormats(exportRoiZipCheckbox, exportCsvCheckbox, exportTxtCheckbox);
                if (!exportFormats.isEmpty()) {
                    appendWithComma(sb, "the resulting contours exported to " + exportFormats);
                }
            }
            
            // If no options selected
            if (sb.length() == 0) {
                sb.append("no output is expected");
            }
            
            outputSummaryProperty.set(sb.toString());
        };
        
        // Add listeners to all checkboxes
        preserveOriginalInputCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());
        addToRoiManagerCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());
        preserveOriginalAnnotationsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());
        imageWithOverlayCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());
        exportRoiZipCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());
        exportCsvCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());
        exportTxtCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());
        
        // Initial update
        updateSummary.run();
        
        // Bind the text property
        outputOptionsSummary.textProperty().bind(outputSummaryProperty);
    }
    
    private void appendWithComma(StringBuilder sb, String text) {
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(text);
    }
    
    private String getExportFormats(CheckBox roiZip, CheckBox csv, CheckBox txt) {
        StringBuilder formats = new StringBuilder();
        
        if (roiZip.isSelected()) {
            formats.append("ROI/ZIP");
        }
        
        if (csv.isSelected()) {
            if (formats.length() > 0) formats.append(", ");
            formats.append("CSV");
        }
        
        if (txt.isSelected()) {
            if (formats.length() > 0) formats.append(", ");
            formats.append("TXT");
        }
        
        return formats.toString();
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