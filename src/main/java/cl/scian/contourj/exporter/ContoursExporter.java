package cl.scian.contourj.exporter;

import org.scijava.Context;

public abstract class ContoursExporter<Contours> extends AbstractDataExporter<Contours> {
    public ContoursExporter(Context context) {
        super(context);
    }
}
