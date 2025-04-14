package cl.scian.contourj.exporter;

import cl.scian.contourj.model.Contour;
import cl.scian.contourj.model.Contours;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class IJ1RoiContourExporter extends ContoursExporter<Contours> {

    @Parameter
    private LogService log;

    public static String NAME = "IJ1 Roi Exporter";
    public static String DESCRIPTION = "An ImageJ1 Roi Exporter. This operation will clear the Roi Manager.";
    public static String EXTENSION = ".zip";
    public static String EXTENSION_DESCRIPTION = "ZIP File (*.zip)";
    public static List<String> EXTENSION_FILTERS = Arrays.asList("*.zip");

    public IJ1RoiContourExporter(Context context) {
        super(context);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getExtension() {
        return EXTENSION;
    }

    @Override
    public List<String> getExtensionFilters() {
        return EXTENSION_FILTERS;
    }

    @Override
    public String getExtensionDescription() {
        return EXTENSION_DESCRIPTION;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String toString() {
        return NAME;
    }

    @Override
    public void export(Contours contours, File file) {
        RoiManager rm = RoiManager.getInstance();
        if (rm == null) {
            rm = RoiManager.getRoiManager();
        }

        rm.runCommand("Deselect");
        if (rm.getRoisAsArray().length > 0) {
            rm.runCommand("Delete");
        }

        for (Contour contour : contours) {
            Roi roi = contour.getRoi();

            roi.setPosition(-1, -1, -1);
            roi.setName(Integer.toString(contour.getId()));
            roi.setStrokeColor(contour.getColor());

            rm.addRoi(roi);
        }

        rm.runCommand("Save", file.getAbsolutePath());
        rm.runCommand("Deselect");
        if (rm.getRoisAsArray().length > 0) {
            rm.runCommand("Delete");
        }

    }
}
