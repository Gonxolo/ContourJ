package cl.scian.contourj;

import cl.scian.contourj.adjustment.ActiveContours;
import cl.scian.contourj.adjustment.ContourAdjuster;
import cl.scian.contourj.model.Contour;
import cl.scian.contourj.model.ContourAdjustmentParameters;
import cl.scian.contourj.model.Contours;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import net.imagej.display.ImageDisplay;
import org.scijava.Context;
import org.scijava.Initializable;
import org.scijava.event.EventService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.ui.UIService;

import java.io.File;
import java.io.IOException;

public class ContourWorkflow implements Initializable {

    @Parameter
    private Context context;

    @Parameter
    private LogService log;

    @Parameter
    private UIService ui;

    @Parameter
    private EventService eventService;

    private final ContourAdjuster contourAdjuster;

    private Contours initialContours;
    private Contours adjustedContours;

    public ContourWorkflow(Context context) {
        context.inject(this);
        this.contourAdjuster = new ActiveContours();
        this.contourAdjuster.setContext(context);
    }

    public ImageDisplay getSourceDisplay() {
        return this.contourAdjuster.getSourceDisplay();
    }

    public void setSourceDisplay(ImageDisplay imd) {
        this.contourAdjuster.setSourceDisplay(imd);
    }

    public ContourAdjustmentParameters parameters() {
        return this.contourAdjuster.parameters();
    }

    public void processMask() {

    }

    private void getContoursFromROIManager() {
        /* TODO: Handle edge cases:
            - 0 contours: Give warning in status bar that no contours where found on the ROI Manager
         */
        RoiManager rm = RoiManager.getInstance();

        if (rm == null) {
            log.info("Roi Manager not found.");
            return;
        }


        Roi[] rois = rm.getRoisAsArray();
        this.initialContours = new Contours(rois.length);

        for (int i = 0; i < rois.length; i++) {
            Roi roi = rois[i];
            float[] x = roi.getFloatPolygon().xpoints;
            float[] y = roi.getFloatPolygon().ypoints;
            this.initialContours.add(new Contour(x, y, roi.getTPosition(), roi.getZPosition()));
            // TODO: If not preserveOriginalROIS -> deleteROI (NOTE: rois in the rm are index in 1, not 0)
        }

        // Set progress bar status based on the number of contours
        // TODO: displayTotalDetectedContours (for status bar progress)

    }

    private void addContoursToROIManager(Contours contours) {
        /* TODO: Handle edge cases:
            - 0 contours: Give warning in status bar that no contours where found on the ROI Manager
         */
        RoiManager rm = RoiManager.getInstance();

        if (rm == null) {
            rm = RoiManager.getRoiManager();
        }
        for (Contour contour : contours) {
            Roi contourAsRoi = contour.getRoi();
            contourAsRoi.setStrokeColor(contour.getColor()); // TODO: Check better way to handle color
            rm.addRoi(contourAsRoi);
        }
    }

    public void initAdjustment() {

        /* TODO: Check selected "processing/output" options
            - Should the original image be preserved? -> Make a copy of the original image (and operate over it instead)
            - Should the original annotations be preserved? -> Ensure they are not deleted after the execution
            - Should the results be displayed as a new image with overlay? -> This should create an image with the ROIs
                as an overlay (not sure yet what that means in Fiji terms)
            - Should the results be added to the ROI Manager? -> This is related to the overlay thing, maybe I want that
                and not ROIs (for some reason)
            - Same as the previous point maybe should consider the option of returning masks as well
         */

        // Check annotation method
        /* TODO: If mask -> transform to rois and add them to ROI Manager
            1. Image > Adjust > Threshold
            2. Analyze > Analyze Particles...
            3. Add results to ROI Manager (most likely this will be forced to delete existing ROIs in the ROI Manager)
         */

        // Get the ROIs from the ROI Manager
        this.getContoursFromROIManager();

        contourAdjuster.setInitialContours(this.initialContours);

        log.debug("Pre-adjustment");
        log.debug("Initial Contours:\n");
        log.debug(this.initialContours.info());

        log.debug(contourAdjuster.info());
        contourAdjuster.runAdjustment();

        this.adjustedContours = this.contourAdjuster.getAdjustedContours();

        this.addContoursToROIManager(this.adjustedContours);

        log.debug("Post-adjustment");
        log.debug("Initial Contours:\n");
        log.debug(this.initialContours.info());
        log.debug("Adjusted Contours:\n");
        log.debug(this.adjustedContours.info());
        log.debug(contourAdjuster.info());

    }

    public void setParametersFromFile(File parametersFile) {
        try {
            this.parameters().setFromFile(parametersFile);
        } catch (IOException e) {
            log.info("File error");
            log.warn(e);
        }
    }
}
