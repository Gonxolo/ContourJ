package cl.scian.contourj.gui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;

public class ImageDisplayManager {
    private final ImageDisplayService imds;

    public ImageDisplayManager(ImageDisplayService imds) {
        this.imds = imds;
    }

    public void updateImageDisplaysList(ComboBox<ImageDisplay> comboBox) {
        ObservableList<ImageDisplay> observableImageDisplays = FXCollections.observableList(imds.getImageDisplays());
        Platform.runLater(() -> {
            comboBox.setItems(observableImageDisplays);
        });
    }

    public void updateDatasetInfoDisplay(ImageDisplay display, Label title, Label width, Label height, 
                                       Label depth, Label frames, Label channels, Label bitDepth, Label source) {
        Dataset dataset = imds.getActiveDataset(display);

        title.setText(dataset.getName());
        width.setText(dataset.getWidth() + " [px]");
        height.setText(dataset.getHeight() + " [px]");
        depth.setText(String.valueOf(dataset.getDepth()));
        frames.setText(String.valueOf(dataset.getFrames()));
        channels.setText(String.valueOf(dataset.getChannels()));
        bitDepth.setText(dataset.getTypeLabelLong());
        source.setText(dataset.getSource());
    }
} 