package cl.scian.contourj.gui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

public class UIStateManager {
    
    private StringProperty inputNameProperty = new SimpleStringProperty("<no-image-selected>");
    private StringProperty maskNameProperty = new SimpleStringProperty("<no-mask-selected>");
    
    public void setupUIBindings(RadioButton inputRadio, RadioButton roiRadio,
                              VBox inputSourceOptions, VBox maskOptions,
                              ToggleButton toggleOptionalSettings, VBox optionalSettingsPanel) {
        // Bind input source options visibility to input radio selection
        inputSourceOptions.disableProperty().bind(inputRadio.selectedProperty().not());

        // Bind mask options visibility to ROI manager selection
        maskOptions.disableProperty().bind(roiRadio.selectedProperty());

        // Bind optional settings panel visibility to toggle button selection
        optionalSettingsPanel.visibleProperty().bind(toggleOptionalSettings.selectedProperty());
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
    public void updateRunningState(boolean isRunning, Button runButton, 
                                ProgressBar statusBar, Label statusText) {
        runButton.setDisable(isRunning);
        statusBar.setProgress(isRunning ? -1 : 0);
        statusBar.setVisible(isRunning);
        statusText.setText(isRunning ? "Running..." : "Ready");
    }
} 