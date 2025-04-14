package cl.scian.contourj.adjustment;

import cl.scian.contourj.model.ContourAdjustmentParameters;
import cl.scian.contourj.model.Contours;
import net.imagej.Dataset;
import net.imagej.display.ImageDisplay;
import org.scijava.plugin.AbstractRichPlugin;

public abstract class AbstractContourAdjuster extends AbstractRichPlugin implements ContourAdjuster {

    private String name;

    private boolean useDefaultParameters = false;

    private final ContourAdjustmentParameters defaultAdjustmentParameters;

    private ContourAdjustmentParameters contourAdjustmentParameters;

    private ImageDisplay sourceDisplay;
    private Dataset dataset;

    private Contours initialContours;
    private Contours adjustedContours;

    public AbstractContourAdjuster() {
        this.defaultAdjustmentParameters = new ContourAdjustmentParameters();
        this.contourAdjustmentParameters = new ContourAdjustmentParameters();
    }

    public boolean getUseDefaultParameters() {
        return this.useDefaultParameters;
    }

    public void setUseDefaultParameters(boolean flag) {
        this.useDefaultParameters = flag;
    }
    @Override
    public ImageDisplay getSourceDisplay() {
        return this.sourceDisplay;
    }

    @Override
    public void setSourceDisplay(ImageDisplay imageDisplay) {
        this.sourceDisplay = imageDisplay;
    }

    @Override
    public void setInitialContours(Contours contours) {
        this.initialContours = contours;
    }

    @Override
    public Contours getInitialContours() {
        return this.initialContours;
    }

    @Override
    public Contours getAdjustedContours() {
        return this.adjustedContours;
    }

    @Override
    public void setAdjustedContours(Contours contours) {
        this.adjustedContours = contours;
    }

    public ContourAdjustmentParameters parameters() {
        if (this.getUseDefaultParameters()) {
            return this.defaultAdjustmentParameters;
        }
        return this.contourAdjustmentParameters;
    }

    @Override
    public Dataset getDataset() {
        return this.dataset;
    }

    @Override
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
