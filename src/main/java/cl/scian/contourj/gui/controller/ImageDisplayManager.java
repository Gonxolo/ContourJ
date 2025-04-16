package cl.scian.contourj.gui.controller;

import ij.ImageListener;
import ij.ImagePlus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.legacy.LegacyService;
import org.scijava.display.event.DisplayEvent;
import org.scijava.display.event.DisplayCreatedEvent;
import org.scijava.display.event.DisplayDeletedEvent;
import org.scijava.display.event.DisplayUpdatedEvent;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.log.LogService;

public class ImageDisplayManager implements ImageListener {
    private final ImageDisplayService imds;
    
    @Parameter
    private EventService eventService;
    
    @Parameter
    private LogService log;
    
    @Parameter
    private LegacyService legacyService;

    private ComboBox<ImageDisplay> sourceComboBox;
    private ComboBox<ImageDisplay> maskComboBox;
    
    // Flag to control whether updates are processed
    private boolean updatesEnabled = true;

    public ImageDisplayManager(ImageDisplayService imds, ComboBox<ImageDisplay> sourceComboBox, ComboBox<ImageDisplay> maskComboBox) {
        this.imds = imds;
        this.sourceComboBox = sourceComboBox;
        this.maskComboBox = maskComboBox;
    }
    
    /**
     * Called when this manager is initialized with the context.
     * Register for ImageJ1 legacy image events.
     */
    public void initialize() {
        if (log != null) {
            log.info("Initializing ImageDisplayManager");
        }
        
        // Register as an ImageJ1 listener to get notified of legacy image changes
        ij.ImagePlus.addImageListener(this);
        
        if (log != null) {
            log.info("Registered as ImageJ1 listener");
        }
    }

    /**
     * Enable or disable updates to avoid unnecessary refreshes during processing
     */
    public void setUpdatesEnabled(boolean enabled) {
        this.updatesEnabled = enabled;
        if (enabled) {
            // Force update when re-enabling
            updateAllComboBoxes();
        }
    }

    /**
     * Updates the ComboBox with current image displays
     */
    public void updateImageDisplaysList() {
        // Get available image displays
        ObservableList<ImageDisplay> displays = FXCollections.observableArrayList(imds.getImageDisplays());
        
        // Set up placeholders
        setupComboBox(sourceComboBox, "no-image-selected");
        setupComboBox(maskComboBox, "no-mask-selected");
        
        if (log != null) {
            log.info("Updating displays. Found " + displays.size() + " displays");
        }
        
        // Store the currently selected item to restore it after update
        ImageDisplay selectedSource = sourceComboBox.getSelectionModel().getSelectedItem();
        ImageDisplay selectedMask = maskComboBox.getSelectionModel().getSelectedItem();
        
        // Update the ComboBox items
        Platform.runLater(() -> {
            // Clear and set new items
            sourceComboBox.getItems().clear();
            sourceComboBox.setItems(displays);
            maskComboBox.getItems().clear();
            maskComboBox.setItems(displays);
            
            // If there are no displays, make sure nothing is selected to show the placeholder
            if (displays.isEmpty()) {
                sourceComboBox.getSelectionModel().clearSelection();
                maskComboBox.getSelectionModel().clearSelection();
            } else {
                if (selectedSource != null) {
                    // Try to find and reselect the previously selected item
                    for (ImageDisplay display : displays) {
                        if (display.getName().equals(selectedSource.getName())) {
                            sourceComboBox.getSelectionModel().select(display);
                            break;
                        }
                    }
                    
                    // If we couldn't find the exact same object but there's at least one display,
                    // and nothing is selected, select the first one
                    if (sourceComboBox.getSelectionModel().getSelectedItem() == null) {
                        // Only select first item if it was previously selected
                        if (selectedSource != null) {
                            sourceComboBox.getSelectionModel().select(0);
                        }
                    }
                }
                if (selectedMask != null) {
                    // Try to find and reselect the previously selected item
                    for (ImageDisplay display : displays) {
                        if (display.getName().equals(selectedMask.getName())) {
                            maskComboBox.getSelectionModel().select(display);
                            break;
                        }
                    }
                    
                    // If we couldn't find the exact same object but there's at least one display,
                    // and nothing is selected, select the first one
                    if (maskComboBox.getSelectionModel().getSelectedItem() == null) {
                        // Only select first item if it was previously selected
                        if (selectedMask != null) {
                            maskComboBox.getSelectionModel().select(0);
                        }
                    }
                }
            }
        });
    }
    
    private void setupComboBox(ComboBox<ImageDisplay> comboBox, String placeholder) {
        if (comboBox == null) {
            if (log != null) {
                log.error("setupComboBox called with null comboBox");
            }
            return;
        }
        
        // Set the prompt text (placeholder)
        comboBox.setPromptText(placeholder);

        // Only set up cell factory if not already done
        if (comboBox.getButtonCell() == null || !(comboBox.getButtonCell() instanceof ListCell)) {
            // Set up the button cell (what appears when an item is selected)
            comboBox.setButtonCell(new ListCell<ImageDisplay>() {
                @Override
                protected void updateItem(ImageDisplay item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(placeholder);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            
            // Set up the cell factory (how items appear in the dropdown)
            comboBox.setCellFactory(p -> new ListCell<ImageDisplay>() {
                @Override
                protected void updateItem(ImageDisplay item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            
            // Force ComboBox to show placeholder text by setting converter
            comboBox.setConverter(new StringConverter<ImageDisplay>() {
                @Override
                public String toString(ImageDisplay object) {
                    if (object == null) {
                        return placeholder;
                    }
                    return object.getName();
                }
                
                @Override
                public ImageDisplay fromString(String string) {
                    return null; // not needed for this use case
                }
            });
        }
    }

    public void updateDatasetInfoDisplay(ImageDisplay display, Label title, Label width, Label height, 
                                       Label depth, Label frames, Label channels, Label bitDepth, Label source) {
        if (display == null) return;
        
        Dataset dataset = imds.getActiveDataset(display);
        if (dataset == null) return;

        title.setText(dataset.getName());
        width.setText(dataset.getWidth() + " [px]");
        height.setText(dataset.getHeight() + " [px]");
        depth.setText(String.valueOf(dataset.getDepth()));
        frames.setText(String.valueOf(dataset.getFrames()));
        channels.setText(String.valueOf(dataset.getChannels()));
        bitDepth.setText(dataset.getTypeLabelLong());
        source.setText(dataset.getSource());
    }
    
    /**
     * Updates all registered ComboBoxes
     */
    public void updateAllComboBoxes() {
        // Skip updates if disabled
        if (!updatesEnabled) {
            if (log != null) {
                log.info("Updates are disabled, skipping ComboBox refresh");
            }
            return;
        }
        
        Platform.runLater(() -> {
            updateImageDisplaysList();
        });
    }
    
    /**
     * ImageJ1 ImageListener implementation
     * Called when a new image is opened
     */
    @Override
    public void imageOpened(ImagePlus imp) {
        if (!updatesEnabled) return;
        
        if (log != null) {
            log.info("ImageJ1 imageOpened event: " + imp.getTitle());
        }
        updateAllComboBoxes();
    }
    
    /**
     * ImageJ1 ImageListener implementation
     * Called when an image is closed
     */
    @Override
    public void imageClosed(ImagePlus imp) {
        if (!updatesEnabled) return;
        
        if (log != null) {
            log.info("ImageJ1 imageClosed event: " + imp.getTitle());
        }
        updateAllComboBoxes();
    }
    
    /**
     * ImageJ1 ImageListener implementation
     * Called when an image is updated
     */
    @Override
    public void imageUpdated(ImagePlus imp) {
        if (!updatesEnabled) return;
        
        if (log != null) {
            log.info("ImageJ1 imageUpdated event: " + imp.getTitle());
        }
        updateAllComboBoxes();
    }
    
    /**
     * Handle display created events
     */
    @EventHandler
    public void onDisplayCreated(DisplayCreatedEvent event) {
        if (!updatesEnabled) return;
        
        if (log != null) {
            log.info("Display created event received");
        }
        updateAllComboBoxes();
    }
    
    /**
     * Handle display deleted events
     */
    @EventHandler
    public void onDisplayDeleted(DisplayDeletedEvent event) {
        if (!updatesEnabled) return;
        
        if (log != null) {
            log.info("Display deleted event received");
        }
        updateAllComboBoxes();
    }
    
    /**
     * Handle display updated events
     */
    @EventHandler
    public void onDisplayUpdated(DisplayUpdatedEvent event) {
        if (!updatesEnabled) return;
        
        if (log != null) {
            log.info("Display updated event received");
        }
        updateAllComboBoxes();
    }
    
    /**
     * Handle any display event to ensure we don't miss updates
     */
    @EventHandler
    public void onDisplayEvent(DisplayEvent event) {
        if (!updatesEnabled) return;
        
        if (log != null) {
            log.info("Display event received: " + event.getClass().getSimpleName());
        }
        updateAllComboBoxes();
    }
} 