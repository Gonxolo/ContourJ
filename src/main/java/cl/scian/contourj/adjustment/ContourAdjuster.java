package cl.scian.contourj.adjustment;

import cl.scian.contourj.model.ContourAdjustmentParameters;
import cl.scian.contourj.model.Contours;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import org.scijava.Named;
import org.scijava.plugin.RichPlugin;

public interface ContourAdjuster extends Named, RichPlugin {
    ImageDisplay getSourceDisplay();

    void setSourceDisplay(ImageDisplay imageDisplay);

    Contours getInitialContours();

    void setInitialContours(Contours contours);

    Contours getAdjustedContours();

    void setAdjustedContours(Contours contours);

    Dataset getDataset();

    void setDataset(Dataset dataset);

    ContourAdjustmentParameters parameters();

    void runAdjustment();

    String info();
}